package com.atsushini.hedgedocportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.helper.UserHelper;
import com.atsushini.hedgedocportal.service.FolderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1/folders")
@RequiredArgsConstructor
public class FolderApiController {
    
    private final FolderService folderService;

    private final UserHelper userHelper;

    @GetMapping
    public ResponseEntity<List<FolderDto>> getFolders(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("no session. set cookie.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // HedgeDocのユーザーID
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = userHelper.getUserId(request);
            session.setAttribute("userId", userId);
        }

        if (userId == null) {
            System.out.println("cookie is wrong");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<FolderDto> folderTree = folderService.getFolderTree(userId);
        return ResponseEntity.ok().body(folderTree);
    }
    
    @GetMapping("/{id}")
    public FolderDto getFolderById(@PathVariable Long id) {
        return folderService.getById(id);
    }

}
