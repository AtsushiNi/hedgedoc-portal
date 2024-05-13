package com.atsushini.hedgedocportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/notes")
@RequiredArgsConstructor
public class NoteApiController {
    
    private final NoteService noteService;

    @PostMapping("/{id}/move")
    public ResponseEntity<String> moveNote(@PathVariable Long id, @RequestBody MoveNoteRequest request) {
        try {
            noteService.moveNote(id, request.getFromFolderId(), request.getToFolderId());
            return ResponseEntity.ok("Note moved successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move note: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(HttpServletRequest request, @PathVariable Long id) {

        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUser = (CurrentUserDto) session.getAttribute("currentUser");

        try {
            noteService.deleteNote(id, currentUser.getId(), currentUser.getCookie());
            return ResponseEntity.ok("deleted note successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @Data
    public static class MoveNoteRequest {

        private Long fromFolderId;
        private Long toFolderId;
    }
}
