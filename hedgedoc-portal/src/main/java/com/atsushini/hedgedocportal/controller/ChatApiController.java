package com.atsushini.hedgedocportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.service.ChatService;

import lombok.Data;

@RestController
@RequestMapping("api/v1/chat")
public class ChatApiController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/extract-keywords")
    public ResponseEntity<String> extractKeywords(@RequestBody ExtractKeywordsRequest request) {
        String keywords = chatService.extractKeywords(request.getQuestion());
        return ResponseEntity.ok(keywords);
    }

    @PostMapping("/search")
    public ResponseEntity<String> search(@RequestBody SearchRequest request) {
        String searchResults = chatService.search(request.getKeywords());
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestBody AnswerRequest request) {
        String answer = chatService.answer(request.getQuestion(), request.getSearchResults());
        return ResponseEntity.ok(answer);
    }

    @Data
    private static class ExtractKeywordsRequest {
        private String question;
    }

    @Data
    private static class SearchRequest {
        private String keywords;
    }

    @Data
    private static class AnswerRequest {
        private String question;
        private String searchResults;
    }
}
