package com.atsushini.hedgedocportal.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
public class MeApiController {

    private final RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<String> getMe(HttpServletRequest request) {

        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String apiUrl = "http://localhost:3000/me";

        // HedgeDocのCookieをセット
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", (String)session.getAttribute("cookie"));

        // HedgeDocからアカウント情報を取得
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        System.out.println(response);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        } else {
            System.out.println("Failed to fetch data from external API");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
