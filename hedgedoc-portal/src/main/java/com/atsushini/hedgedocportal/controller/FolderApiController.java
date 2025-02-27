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

import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.FolderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/folders")
@RequiredArgsConstructor
public class FolderApiController {
    
    private final FolderService folderService;

    @GetMapping
    public ResponseEntity<List<FolderDto>> getFolders(HttpServletRequest request) {
        List<FolderDto> folderTree = folderService.getFolderTree();
        return ResponseEntity.ok().body(folderTree);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FolderDto> getFolderById(@PathVariable Long id) {
        return ResponseEntity.ok().body(folderService.getById(id));
    }

    @PostMapping
    public ResponseEntity<String> createFolder(HttpServletRequest request, @RequestBody CreateRequest requestBody) {
        folderService.create(requestBody.getTitle(), requestBody.getParentFolderId());

        return ResponseEntity.ok("created folder successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateFolder(@PathVariable Long id, @RequestBody UpdateFolderRequest request) {
        try {
            folderService.updateFolder(id, request.getTitle());
            return ResponseEntity.ok("Folder updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update folder: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<String> moveFolder(@PathVariable Long id, @RequestBody MoveFolderRequest request) {
        try {
            folderService.moveFolder(id, request.getToFolderId());
            return ResponseEntity.ok("Folder move successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move folder: " + e.getMessage());
        }
    }

    // フォルダーを削除する
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long id) {
        try {
            folderService.deleteFolder(id);
            return ResponseEntity.ok("Folder deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete folder: " + e.getMessage());
        }
    }

    // フォルダーからノートを削除する
    @DeleteMapping("/{id}/notes/{noteId}")
    public ResponseEntity<String> deleteNoteFromFolder(HttpServletRequest request, @PathVariable Long id, @PathVariable Long noteId) {
        try {
            folderService.deleteNoteFromFolder(id, noteId);
            return ResponseEntity.ok("deleted note from folder successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @Data
    public static class CreateRequest {
        private String title;
        private Long parentFolderId;
    }

    @Data
    public static class UpdateFolderRequest {
        private String title;
    }

    @Data
    public static class MoveFolderRequest {
        private Long toFolderId;
    }
}
