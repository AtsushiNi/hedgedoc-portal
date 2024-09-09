package com.atsushini.hedgedocportal.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.service.ESNoteService;
import com.atsushini.hedgedocportal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/login")
@RequiredArgsConstructor
public class LoginApiController {

    private final UserService userService;
    private final ESNoteService noteService;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    @PostMapping
    public void login(@RequestBody PostBody requestBody, HttpServletRequest request) throws Exception {
        System.out.println(noteService.searchNotesByContent("これは"));
        System.out.println("================================-");

        RestTemplate restTemplate = new RestTemplate();

        // http://localhost:3000にGETリクエストを送信
        URI hedgedocHomeUrl = new URI(hedgedocUrl);
        ResponseEntity<String> response = restTemplate.exchange(hedgedocHomeUrl, HttpMethod.GET, null, String.class);
        String hedgedocCookies = extractCookies(response.getHeaders());
        hedgedocCookies = "connect.sid=" + extractCookieValue(hedgedocCookies, "connect.sid");
        System.out.println("URL: " + hedgedocHomeUrl);
        System.out.println("Initial status: " + response.getStatusCode());
        System.out.println("HedgeDoc cookies: " + hedgedocCookies);

        // SAMLのリダイレクトに対応
        URI samlUrl = new URI(hedgedocUrl + "/auth/saml");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", hedgedocCookies);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        response = restTemplate.exchange(samlUrl, HttpMethod.GET, requestEntity,
                String.class);
        String keycloakCookies = extractCookies(response.getHeaders()) + "; " + hedgedocCookies;
        System.out.println("URL: " + samlUrl);
        System.out.println("SAML redirect status: " + response.getStatusCode());
        System.out.println("Keycloak cookies: " + keycloakCookies);

        // HTMLを解析してフォームのactionを取得
        Document document = Jsoup.parse(response.getBody());
        Element form = document.selectFirst("form");
        String nextURL = form.attr("action");

        // フォームデータを送信
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", requestBody.getUserId());
        formData.add("password", requestBody.getPassword());
        requestHeaders.set("Cookie", keycloakCookies);
        HttpEntity<MultiValueMap<String, String>> formRequestEntity = new HttpEntity<>(formData, requestHeaders);
        response = restTemplate.exchange(URI.create(nextURL), HttpMethod.POST,
                formRequestEntity, String.class);
        System.out.println("URL: " + nextURL);
        System.out.println("Form submission status: " + response.getStatusCode());

        // HTMLを解析してフォームのactionを取得
        document = Jsoup.parse(response.getBody());
        form = document.selectFirst("form");
        Element input = form.selectFirst("input");
        String SAMLResponse = input.attr("value");
        System.out.println(SAMLResponse);

        // コールバックを送信
        formData = new LinkedMultiValueMap<>();
        formData.add("SAMLResponse", SAMLResponse);
        requestHeaders.set("Cookie", hedgedocCookies);
        formRequestEntity = new HttpEntity<>(formData, requestHeaders);
        response = restTemplate.exchange(hedgedocUrl + "/auth/saml/callback", HttpMethod.POST,
                formRequestEntity, String.class);
        hedgedocCookies = extractCookies(response.getHeaders());
        System.out.println("URL: " + hedgedocUrl + "/auth/saml/callback");
        System.out.println("Form submission status: " + response.getStatusCode());

        // ログインできたか検証
        requestHeaders.set("Cookie", hedgedocCookies);
        HttpEntity<String> hedgedocEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<String> hedgedocResponse = restTemplate.exchange(hedgedocUrl, HttpMethod.GET, hedgedocEntity, String.class);
        if (hedgedocResponse.getStatusCode() == HttpStatusCode.valueOf(302)) {
            throw new Exception("forbidden");
        }

        CurrentUserDto user = userService.getUserByCookie(hedgedocCookies);
        // セッションにユーザーを保存
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);
    }

    @Data
    public static class PostBody {
        private String userId;
        private String password;
    }

    // HttpHeadersからcookieの文字列を取得する
    private String extractCookies(HttpHeaders headers) {
        Map<String, String> cookies = new HashMap<>();
        headers.get("Set-Cookie").forEach(cookie -> {
            String[] cookieParts = cookie.split(";");
            Arrays.stream(cookieParts).forEach(part -> {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2) {
                    cookies.put(keyValue[0].trim(), keyValue[1].trim());
                }
            });
        });
        return cookiesToString(cookies);
    }

    // cookieのMapからcookie文字列を作成する
    private String cookiesToString(Map<String, String> cookies) {
        StringBuilder cookieString = new StringBuilder();
        cookies.forEach((key, value) -> {
            if (cookieString.length() > 0) {
                cookieString.append("; ");
            }
            cookieString.append(key).append("=").append(value);
        });
        return cookieString.toString();
    }

    // 指定したkeyに対応するcookieのvalueを取得する
    public String extractCookieValue(String cookieString, String cookieName) {
        // クッキー文字列をセミコロンで分割して各属性を取得
        String[] cookies = cookieString.split(";\\s*");

        // 各属性をループして指定されたクッキー名を探す
        for (String cookie : cookies) {
            if (cookie.startsWith(cookieName + "=")) {
                // 指定されたクッキー名に一致する場合、その値を返す
                return cookie.substring((cookieName + "=").length());
            }
        }

        // 指定されたクッキー名が見つからない場合はnullを返す
        return null;
    }
}
