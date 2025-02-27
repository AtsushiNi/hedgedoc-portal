package com.atsushini.hedgedocportal.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoAuthenticationException extends RuntimeException {
    public NoAuthenticationException(String message) {
        super(message);
    }

    public NoAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
