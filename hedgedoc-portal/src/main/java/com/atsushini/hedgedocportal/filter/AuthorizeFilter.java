package com.atsushini.hedgedocportal.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizeFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher loginMatcher = new AntPathRequestMatcher("/api/v1/login");
    private final AntPathRequestMatcher apiMatcher = new AntPathRequestMatcher("/api/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (apiMatcher.matches(request) && !loginMatcher.matches(request)) {
                String xAuthToken = request.getHeader("X-AUTH-TOKEN");
                if (xAuthToken == null || !xAuthToken.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("__secret__")).build()
                        .verify(xAuthToken.substring(7));
                String username = decodedJWT.getClaim("userId").toString();
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
