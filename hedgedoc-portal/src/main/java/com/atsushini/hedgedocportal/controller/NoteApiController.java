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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.ESNoteService;
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
    private final ESNoteService esNoteService;

    @GetMapping("/search")
    public ResponseEntity<List<NoteDto>> search(@RequestParam(name = "query", defaultValue = "default search") String query) {
        List<NoteDto> results = esNoteService.searchNotesByContent(query);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("synchronize-search")
    public ResponseEntity<String> synchronizeSearch(HttpServletRequest request) {
        esNoteService.synchronize();
        return ResponseEntity.ok("ok");
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

        try {
            noteService.deleteNote(id);
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
