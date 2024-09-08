package com.atsushini.hedgedocportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
    
    private final RestTemplate restTemplate;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    // HedgeDocの履歴を返す
    public HistoryDto getHistory(CurrentUserDto currentUserDto) {
        try {
            String apiUrl = hedgedocUrl + "/history";

            // HedgeDocのCookieをセット
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", currentUserDto.getCookie());

            // HedgeDocから履歴を検索
            ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), HistoryDto.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Failed to fetch data from external API");
                throw new HedgedocApiException("Failed to fetch data from external API");
            }

            return response.getBody();
        } catch (Exception e) {
            // HedgeDocの認証が失敗すると、ステータス200でログインページが表示される。
            // レスポンスがHistortyDtoに変換失敗すると、認証失敗と判定する。
            System.out.println("Failed to parse data from external API. Maybe forbidden.");
            throw new HedgedocForbiddenException("Failed to parse data from external API. Maybe forbidden.");
        }

    }
}
