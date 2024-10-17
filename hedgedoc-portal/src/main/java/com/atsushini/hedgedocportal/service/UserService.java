package com.atsushini.hedgedocportal.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    public CurrentUserDto getUserByCookie(String cookie) {
        String apiUrl = hedgedocUrl + "/me";

        // HedgeDocからアカウント情報を取得
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        System.out.println("headers: " + headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        String responseBody = response.getBody();
        System.out.println("res: " + responseBody);

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
}
