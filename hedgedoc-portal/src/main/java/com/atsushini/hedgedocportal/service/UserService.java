package com.atsushini.hedgedocportal.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.entity.User;
import com.atsushini.hedgedocportal.exception.ForbiddenException;
import com.atsushini.hedgedocportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    public final RestTemplate restTemplate;
    public final UserRepository userRepository;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    // ユーザー名とパスワードからユーザー情報を取得する
    public UserDto getUserByNameAndPassword(String userName, String password) {

        try {
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
            formData.add("username", userName);
            formData.add("password", password);
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
            ResponseEntity<String> hedgedocResponse = restTemplate.exchange(hedgedocUrl + "/me", HttpMethod.GET, hedgedocEntity, String.class);
            if (hedgedocResponse.getStatusCode() == HttpStatusCode.valueOf(302)) {
                throw new ForbiddenException();
            }
            // レスポンスから"id"フィールド(HedgedocのユーザーID)を取得
            String hedgedocUserId = hedgedocResponse.getBody().split("\"id\":\"")[1].split("\"")[0];

            // DBからユーザーを検索. 存在しなければ作成.
            User user = userRepository.findByHedgedocId(hedgedocUserId);
            if (user == null) {
                user = new User();
                user.setHedgedocId(hedgedocUserId);
                user = userRepository.save(user);
            }
            // DTOに詰め替えて返す
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setHedgedocId(user.getHedgedocId());
            dto.setHedgedocCookies(hedgedocCookies);
            return dto;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object[]> getCumulativeUserCount() {
        // 日別のユーザー登録数を取得
        List<Object[]> dailyCounts = userRepository.findUserCountPerDay();
        
        // 最初の日付と現在の日付の間の全日付を取得
        LocalDate startDate = (LocalDate) dailyCounts.get(0)[0];
        LocalDate endDate = LocalDate.now();
        
        // 日別ユーザー登録数をマップに変換 (日付 -> 登録数)
        Map<LocalDate, Long> dailyCountMap = new HashMap<>();
        for (Object[] dailyCount : dailyCounts) {
            LocalDate date = (LocalDate) dailyCount[0];
            Long count = (Long) dailyCount[1];
            dailyCountMap.put(date, count);
        }
        
        // 累計ユーザー数を計算
        List<Object[]> cumulativeCounts = new ArrayList<>();
        long cumulativeSum = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            long count = dailyCountMap.getOrDefault(date, 0L); // ユーザー登録のない日は0
            cumulativeSum += count; // 累計ユーザー数を加算
            cumulativeCounts.add(new Object[]{date, cumulativeSum});
        }

        return cumulativeCounts;
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
    private String extractCookieValue(String cookieString, String cookieName) {
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
