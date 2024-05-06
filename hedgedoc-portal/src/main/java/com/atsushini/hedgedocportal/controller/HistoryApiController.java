package com.atsushini.hedgedocportal.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.HistoryDto;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("history")
@RequiredArgsConstructor
public class HistoryApiController {

    private final RestTemplate restTemplate;

    @GetMapping
    public HistoryDto getHistory() {
        String apiUrl = "http://localhost:3000/history";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "indent_type=space; space_units=4; keymap=sublime; JSESSIONID=26F00C3087C1D6D67305EEF994320891; connect.sid=s%3An5YJPKRLsDyGUj0f0gfHaShdHWISM4bM.H7DWlQ9uyCKB40RYOAJgZUSp9WT6Kp1HiP2ASaXvuIY; loginstate=true; userid=458a7e5e-902f-4fa2-83ce-bde47a86dd0a");

        ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), HistoryDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            HistoryDto historyDto = response.getBody();
            return historyDto;
        } else {
            System.out.println("Failed to fetch data from external API");
            return null;
        }
    }
        
}
