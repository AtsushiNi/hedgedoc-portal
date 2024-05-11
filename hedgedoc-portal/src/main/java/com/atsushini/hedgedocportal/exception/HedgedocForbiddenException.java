package com.atsushini.hedgedocportal.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HedgedocForbiddenException extends RuntimeException {
    public HedgedocForbiddenException(String message) {
        super(message);
    }

    public HedgedocForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
