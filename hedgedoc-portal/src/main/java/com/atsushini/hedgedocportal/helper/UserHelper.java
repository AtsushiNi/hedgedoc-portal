package com.atsushini.hedgedocportal.helper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelper {
    
    private final RestTemplate restTemplate;

    public String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");

        String apiUrl = "http://localhost:3000/me";

        // HedgeDocのCookieをセット
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", (String)session.getAttribute("cookie"));

        // HedgeDocからアカウント情報を取得
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        String responseBody = response.getBody();

        // レスポンスから"id"フィールドを取得
        userId = responseBody.contains("\"id\":\"") ? responseBody.split("\"id\":\"")[1].split("\"")[0] : null;

        return userId;
    }
}
