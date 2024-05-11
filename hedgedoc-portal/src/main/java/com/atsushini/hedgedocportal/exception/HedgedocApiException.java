package com.atsushini.hedgedocportal.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HedgedocApiException extends RuntimeException {
    public HedgedocApiException(String message) {
        super(message);
    }

    public HedgedocApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
