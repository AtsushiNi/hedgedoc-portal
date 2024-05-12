package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.service.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("api/v1/history")
@RequiredArgsConstructor
public class HistoryApiController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getHistory(HttpServletRequest request) {

        // sessionかcookieがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUserDto = (CurrentUserDto) session.getAttribute("currentUser");

        try {
            List<NoteDto> history = noteService.getHistory(currentUserDto);
            return ResponseEntity.ok(history);
        } catch (HedgedocForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (HedgedocApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
