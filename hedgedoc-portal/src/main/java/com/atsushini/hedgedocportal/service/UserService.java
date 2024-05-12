package com.atsushini.hedgedocportal.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.entity.User;
import com.atsushini.hedgedocportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    public final RestTemplate restTemplate;
    public final UserRepository userRepository;
    
    public CurrentUserDto getUserByCookie(String cookie) {
        String apiUrl = "http://localhost:3000/me";

        // HedgeDocからアカウント情報を取得
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        String responseBody = response.getBody();

        // レスポンスから"id"フィールド(HedgedocのユーザーID)を取得
        String hedgedocUserId = responseBody.contains("\"id\":\"")
            ? responseBody.split("\"id\":\"")[1].split("\"")[0]
            : null;

        // DBからユーザーを検索. 存在しなければ作成.
        User user = userRepository.findByHedgedocId(hedgedocUserId);
        if (user == null) {
            user = new User();
            user.setHedgedocId(hedgedocUserId);
            user = userRepository.save(user);
        }

        // DTOに詰め替えて返す
        CurrentUserDto dto = new CurrentUserDto();
        dto.setId(user.getId());
        dto.setHedgedocId(user.getHedgedocId());
        dto.setCookie(cookie);
        return dto;
    }
}
