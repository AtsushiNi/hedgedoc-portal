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

import com.atsushini.hedgedocportal.dto.HistoryDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("api/v1/history")
@RequiredArgsConstructor
public class HistoryApiController {

    private final RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<HistoryDto> getHistory(HttpServletRequest request) {

        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            String apiUrl = "http://localhost:3000/history";

            // HedgeDocのCookieをセット
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", (String)session.getAttribute("cookie"));

            // HedgeDocから履歴を検索
            ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), HistoryDto.class);
            System.out.println("HedgeDoc /history response: ");
            System.out.println(response);

            if (response.getStatusCode().is2xxSuccessful()) {
                HistoryDto historyDto = response.getBody();
                return ResponseEntity.ok(historyDto);
            } else {
                System.out.println("Failed to fetch data from external API");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            // HedgeDocの認証が失敗すると、ステータス200でログインページが表示される。
            // レスポンスがHistortyDtoに変換失敗すると、認証失敗と判定する。
            System.out.println("Failed to parse data from external API. Maybe forbidden.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
        
}
