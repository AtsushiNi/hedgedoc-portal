package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.service.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/history")
@RequiredArgsConstructor
public class HistoryApiController {
    
    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getHistory(HttpServletRequest request) {
        try {
            List<NoteDto> history = noteService.getHomeNotes();
            return ResponseEntity.ok(history);
        } catch (HedgedocForbiddenException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (HedgedocApiException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
