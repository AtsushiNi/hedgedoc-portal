package com.atsushini.hedgedocportal.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    private final RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    private String elasticSearchUrl;
    @Value("${deepseek.api.url}")
    private String deepSeekUrl; 
    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    public String extractKeywords(String text) {
        HttpHeaders deepSeekHeaders = new HttpHeaders();
        deepSeekHeaders.set("Content-Type", "application/json");
        deepSeekHeaders.set("Authorization", "Bearer " + deepSeekApiKey);
        
        String escapedText = text.replaceAll("[\u0000-\u001F]", ""); // 制御文字を除去
        String deepSeekBody = String.format("""
            {
              "model": "deepseek-chat",
              "messages": [
                {
                  "role": "user",
                  "content": "以下のテキストからキーワードをいくつか考えて。複数ある場合は半角スペースで区切って\\n%s"
                }
              ],
              "stream": false
            }""", escapedText);
        
        HttpEntity<String> deepSeekEntity = new HttpEntity<>(deepSeekBody, deepSeekHeaders);
        ResponseEntity<String> deepSeekResponse = restTemplate.exchange(
            deepSeekUrl, HttpMethod.POST, deepSeekEntity, String.class);
        
        try {
            DeepSeekResponse deepSeekResponseObj = (new ObjectMapper()).readValue(
                deepSeekResponse.getBody(), DeepSeekResponse.class);
            try {
                String jsonLog = objectMapper.writeValueAsString(deepSeekResponseObj.getChoices());
                logger.info("DeepSeek response choices:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.info("DeepSeek response choices: {}", deepSeekResponseObj.getChoices());
            }
            return deepSeekResponseObj.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            errorData.put("stackTrace", Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()));
            try {
                String jsonLog = objectMapper.writeValueAsString(errorData);
                logger.error("Failed to parse DeepSeek response:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.error("Failed to parse DeepSeek response: {}", errorData);
            }
            throw new RuntimeException("Failed to parse DeepSeek API response. Please check the logs for more details.", e);
        }
    }

    public String search(String keywords) {
        String jsonQuery = String.format("""
            {
              "query": {
                "match": {
                  "content": {
                    "query": "%s",
                    "analyzer": "kuromoji_analyzer"
                  }
                }
              },
              "size": 10,
              "_source": false,
              "highlight": {
                "fields": {
                  "content": {}
                }
              }
            }""", keywords);
            
        String url = elasticSearchUrl + "/note/_search";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);
        
        try {
            SearchResponse responseObject = (new ObjectMapper()).readValue(response.getBody(), SearchResponse.class);
            try {
                String jsonLog = objectMapper.writeValueAsString(responseObject);
                logger.info("Elasticsearch response:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.info("Elasticsearch response: {}", responseObject);
            }
            
            StringBuilder resultBuilder = new StringBuilder();
            for (SearchResponse.Hit hit : responseObject.getHits().getHits()) {
                if (hit.getHighlight() != null && hit.getHighlight().getContent() != null) {
                    for (String highlight : hit.getHighlight().getContent()) {
                        resultBuilder.append("- ").append(highlight).append("\n");
                    }
                }
            }
            return resultBuilder.toString();
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            errorData.put("stackTrace", Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()));
            try {
                String jsonLog = objectMapper.writeValueAsString(errorData);
                logger.error("Failed to parse Elasticsearch response:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.error("Failed to parse Elasticsearch response: {}", errorData);
            }
            throw new RuntimeException("Failed to parse Elasticsearch response. Please check the logs for more details.", e);
        }
    }

    public String answer(String question, String searchResults) {
        String deepSeekUrl = "https://api.deepseek.com/chat/completions";
        HttpHeaders deepSeekHeaders = new HttpHeaders();
        deepSeekHeaders.set("Content-Type", "application/json");
        deepSeekHeaders.set("Authorization", "Bearer " + System.getenv("DEEPSEEK_API_KEY"));
        
        String escapedQuestion = question.replaceAll("[\u0000-\u001F]", ""); // 制御文字を除去
        String answerBody = String.format("""
            {
              "model": "deepseek-chat",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ],
              "stream": false
            }""", escapedQuestion);
        
        HttpEntity<String> answerEntity = new HttpEntity<>(answerBody, deepSeekHeaders);
        ResponseEntity<String> answerResponse = restTemplate.exchange(
            deepSeekUrl, HttpMethod.POST, answerEntity, String.class);
        
        try {
            DeepSeekResponse answerResponseObj = (new ObjectMapper()).readValue(
                answerResponse.getBody(), DeepSeekResponse.class);
            try {
                String jsonLog = objectMapper.writeValueAsString(answerResponseObj.getChoices());
                logger.info("DeepSeek answer response choices:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.info("DeepSeek answer response choices: {}", answerResponseObj.getChoices());
            }
            return answerResponseObj.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            errorData.put("stackTrace", Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()));
            try {
                String jsonLog = objectMapper.writeValueAsString(errorData);
                logger.error("Failed to parse DeepSeek answer response:\n{}", jsonLog);
            } catch (JsonProcessingException e2) {
                logger.error("Failed to parse DeepSeek answer response: {}", errorData);
            }
            throw new RuntimeException("Failed to parse DeepSeek answer response. Please check the logs for more details.", e);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeepSeekResponse {
        private List<Choice> choices;
        
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Choice {
            private Message message;
        }
        
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Message {
            private String content;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SearchResponse {
        private Hits hits;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Hits {
            private List<Hit> hits;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Hit {
            private String _id;
            private double _score;
            private Source _source;
            private Highlight highlight;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Source {
            private String content;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Highlight {
            private List<String> content;
        }
    }
}
