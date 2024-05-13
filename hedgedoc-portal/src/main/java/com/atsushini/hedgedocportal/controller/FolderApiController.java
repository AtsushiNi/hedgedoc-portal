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
import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.service.FolderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/folders")
@RequiredArgsConstructor
public class FolderApiController {
    
    private final FolderService folderService;

    @GetMapping
    public ResponseEntity<List<FolderDto>> getFolders(HttpServletRequest request) {

        // セッションがなければ403を返し、Cookie設定画面に遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        CurrentUserDto userDto = (CurrentUserDto) session.getAttribute(("currentUser"));

        List<FolderDto> folderTree = folderService.getFolderTree(userDto);
        return ResponseEntity.ok().body(folderTree);
    }
    
    @GetMapping("/{id}")
    public FolderDto getFolderById(@PathVariable Long id) {
        return folderService.getById(id);
    }

    @PostMapping
    public ResponseEntity<String> createFolder(HttpServletRequest request, @RequestBody CreateRequest requestBody) {
        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUser = (CurrentUserDto) session.getAttribute("currentUser");

        folderService.create(requestBody.getTitle(), requestBody.getParentFolderId(), currentUser);

        return ResponseEntity.ok("created folder successfully");
    }
    
    // フォルダーからノートを削除する
    @DeleteMapping("/{id}/notes/{noteId}")
    public ResponseEntity<String> deleteNoteFromFolder(HttpServletRequest request, @PathVariable Long id, @PathVariable Long noteId) {
        // sessionがなければ認証エラー。Cookie設定ページへ遷移させる
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        CurrentUserDto currentUser = (CurrentUserDto) session.getAttribute("currentUser");

        // 対象フォルダーの所有者がログインユーザーであることを確認
        Long folderOwnerId = folderService.getOwnerId(id);
        if (!folderOwnerId.equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

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
}
