package com.atsushini.hedgedocportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.NoteService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/notes")
@RequiredArgsConstructor
public class NoteApiController {
    
    private final NoteService noteService;

    @PostMapping("/move")
    public ResponseEntity<String> moveNote(@RequestBody MoveNoteRequest request) {
        try {
            noteService.moveNote(request.getNoteId(), request.getFromFolderId(), request.getToFolderId());
            return ResponseEntity.ok("Note moved successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move note: " + e.getMessage());
        }
    }

    @Data
    public static class MoveNoteRequest {

        private Long noteId;
        private Long fromFolderId;
        private Long toFolderId;
    }
}
