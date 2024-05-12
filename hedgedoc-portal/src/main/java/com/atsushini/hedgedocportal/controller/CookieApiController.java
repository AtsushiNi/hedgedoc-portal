package com.atsushini.hedgedocportal.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/cookie")
@RequiredArgsConstructor
public class CookieApiController {

    private final UserService userService;

    @PostMapping
    public void setCookie(@RequestBody PostBody requestBody, HttpServletRequest request) {

        HttpSession session = request.getSession();

        CurrentUserDto user = userService.getUserByCookie(requestBody.getCookie());
        // セッションにユーザーを保存
        session.setAttribute("currentUser", user);
    }

    public static class PostBody {
        private String cookie;

        public String getCookie() {
            return cookie;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }
    }
}
