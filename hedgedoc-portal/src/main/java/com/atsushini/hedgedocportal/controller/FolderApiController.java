package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.service.FolderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

}
