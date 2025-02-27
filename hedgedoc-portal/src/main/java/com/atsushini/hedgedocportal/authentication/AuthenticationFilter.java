package com.atsushini.hedgedocportal.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atsushini.hedgedocportal.dto.UserDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher loginMatcher = new AntPathRequestMatcher("/api/v1/login");
    private final AntPathRequestMatcher apiMatcher = new AntPathRequestMatcher("/api/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (apiMatcher.matches(request) && !loginMatcher.matches(request)) { // ログイン以外のAPIについてフィルターを通す
            // ヘッダーからユーザー情報を取得しContextに格納
            String xAuthToken = request.getHeader("X-AUTH-TOKEN");
            if (xAuthToken == null || !xAuthToken.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            } 
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("__secret__")).build().verify(xAuthToken.substring(7));
            UserDto user = new UserDto();
            user.setId(Long.parseLong(decodedJWT.getClaim("userId").toString()));
            user.setUserName(decodedJWT.getClaim("userName").toString());
            String hedgedocId = decodedJWT.getClaim("hedgedocId").toString();
            user.setHedgedocId(hedgedocId.substring(1, hedgedocId.length() - 1));
            user.setHedgedocCookies(decodedJWT.getClaim("hedgedocCookies").toString());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>()));
        }
        filterChain.doFilter(request, response);
    }

    // "[element1, element2, element3]"形式のStringをList<String>に変換する
    private List<String> toList(String value) {
        String trimmed = value.substring(2, value.length() - 2);
        return Arrays.asList(trimmed.split(", "));
    }
}
