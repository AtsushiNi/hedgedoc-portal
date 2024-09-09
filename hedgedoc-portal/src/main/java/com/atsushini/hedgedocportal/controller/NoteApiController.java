package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.HistoryService;
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
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotes(HttpServletRequest request) {

        // sessionかcookieがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUserDto = (CurrentUserDto) session.getAttribute("currentUser");

        try {
            List<NoteDto> history = noteService.getUnFolderedNotes(currentUserDto);
            return ResponseEntity.ok(history);
        } catch (HedgedocForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (HedgedocApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteDto>> search(HttpServletRequest request) {
        // sessionかcookieがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUserDto = (CurrentUserDto) session.getAttribute("currentUser");

        // 履歴を取得
        List<NoteDto> history = noteService.getUnFolderedNotes(currentUserDto);
        // HedgeDocのノート内容エクスポートデータを取得
        noteService.storeNoteContents(history);
        // List<NoteDto> exportData = historyService.getExportData(currentUserDto);
        

        return ResponseEntity.ok(history);
    }
    
    @PostMapping
    public ResponseEntity<String> createNote(@RequestBody CreateNoteRequest request) {
        try {
            String newNoteUrl = noteService.createNote(request.getParentFolderId()); 
            return ResponseEntity.ok(newNoteUrl);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create note: " + e.getMessage());
        }
    }

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
    public static class CreateNoteRequest {
        private Long parentFolderId;
    }

    @Data
    public static class MoveNoteRequest {

        private Long fromFolderId;
        private Long toFolderId;
    }
}
