package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.RuleDto;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.RuleService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/rules")
@RequiredArgsConstructor
public class RuleApiController {
    
    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<RuleDto>> getRules(HttpServletRequest request) {

        List<RuleDto> ruleDtoList = ruleService.getRules();
        return ResponseEntity.ok().body(ruleDtoList);
    }

    @PostMapping
    public ResponseEntity<String> createRule(HttpServletRequest request, @RequestBody CreateRequest requestBody) {

        ruleService.create(requestBody.getTitle(), requestBody.getRegularExpression(), requestBody.getFolderId());

        return ResponseEntity.ok("created rule successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRule(HttpServletRequest request, @PathVariable Long id, @RequestBody UpdateRequest requestBody) {

        try {
            ruleService.update(id, requestBody.getTitle(), requestBody.getRegularExpression(), requestBody.getFolderId());
            return ResponseEntity.ok("updated rule successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update rule: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRule(HttpServletRequest request, @PathVariable Long id) {

        try {
            ruleService.delete(id);
            return ResponseEntity.ok("deleted rule successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete rule: " + e.getMessage());
        }
    }

    @Data
    public static class CreateRequest {
        private String title;
        private String regularExpression;
        private Long folderId;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String regularExpression;
        private Long folderId;
    }
}
