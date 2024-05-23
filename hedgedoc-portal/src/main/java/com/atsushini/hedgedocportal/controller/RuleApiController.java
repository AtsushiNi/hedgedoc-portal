package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.RuleDto;
import com.atsushini.hedgedocportal.service.RuleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/rules")
@RequiredArgsConstructor
public class RuleApiController {
    
    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<RuleDto>> getRules(HttpServletRequest request) {
        // セッションがなければ403を返し、Cookie設定画面に遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto userDto = (CurrentUserDto) session.getAttribute(("currentUser"));

        List<RuleDto> ruleDtoList = ruleService.getRules(userDto);
        return ResponseEntity.ok().body(ruleDtoList);
    }

    @PostMapping
    public ResponseEntity<String> createRule(HttpServletRequest request, @RequestBody CreateRequest requestBody) {
        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUser = (CurrentUserDto) session.getAttribute("currentUser");

        ruleService.create(requestBody.getTitle(), requestBody.getRegularExpression(), requestBody.getFolderId(), currentUser);

        return ResponseEntity.ok("created rule successfully");
    }

    @Data
    public static class CreateRequest {
        private String title;
        private String regularExpression;
        private Long folderId;
    }
}
