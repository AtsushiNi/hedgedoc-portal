package com.atsushini.hedgedocportal.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/cookie")
@RequiredArgsConstructor
public class CookieApiController {

    @PostMapping
    public void setCookie(@RequestBody PostBody requestBody, HttpServletRequest request) {

        HttpSession session = request.getSession();
        System.out.println(requestBody.getCookie());
        session.setAttribute("cookie", requestBody.getCookie());
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
