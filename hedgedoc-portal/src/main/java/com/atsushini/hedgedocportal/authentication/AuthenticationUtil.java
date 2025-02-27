package com.atsushini.hedgedocportal.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.exception.NoAuthenticationException;

public class AuthenticationUtil {
    // Contextからログインユーザーの情報を取得
    public static UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NoAuthenticationException("no authentication!");
        }

        return (UserDto) authentication.getPrincipal();
    }
}