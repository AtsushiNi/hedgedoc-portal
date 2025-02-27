package com.atsushini.hedgedocportal.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = ((UserDto)authentication.getPrincipal()).getUserName();
        String password = authentication.getCredentials().toString();

        // Hedgedocにアクセスし、ユーザー情報が正しいことを検証
        UserDto user = userService.getUserByNameAndPassword(userName, password);

        return new UsernamePasswordAuthenticationToken(user, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
