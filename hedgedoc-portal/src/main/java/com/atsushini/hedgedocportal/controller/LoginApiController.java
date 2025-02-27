package com.atsushini.hedgedocportal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.authentication.CustomAuthenticationProvider;
import com.atsushini.hedgedocportal.dto.UserDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/login")
@RequiredArgsConstructor
public class LoginApiController {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody PostBody requestBody, HttpServletRequest request) throws Exception {
        try {
            UserDto user = new UserDto();
            user.setUserName(requestBody.getUserName());
            // 認証
            Authentication authentication = customAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(user, requestBody.getPassword()));

            user = (UserDto) authentication.getPrincipal();
            String token = JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("userName", user.getUserName())
                .withClaim("hedgedocId", user.getHedgedocId())
                .withClaim("hedgedocCookies", user.getHedgedocCookies())
                .sign(Algorithm.HMAC256("__secret__"));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("x-auth-token", token);
            return new ResponseEntity(httpHeaders, HttpStatus.OK);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Data
    public static class PostBody {
        private String userName;
        private String password;
    }
}
