package com.atsushini.hedgedocportal.config;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {
    private final PasswordEncoder encoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDetails user = new LoginUserDetails();
        user.setUsername(username);
        // 本来はDBから取得するべきだが、とりあえずモック
        user.setPassword(encoder.encode("pass"));

        return user;
    }

    @Getter
    @Setter
    public class LoginUserDetails implements UserDetails {
        private String username;
        private String password;
        private List<GrantedAuthority> authorities;
        public boolean isEnabled() {
            return true;
        }
        public boolean isCredentialsNonExpired() {
            return true;
        }
        public boolean isAccountNonExpired() {
            return true;
        }
        public boolean isAccountNonLocked() {
            return true;
        }
    }
}
