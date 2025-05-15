package com.autoria.clone.infrastructure.exception;

public class ProfanityException extends RuntimeException {

    public ProfanityException(String message) {
        super(message);
    }

    public ProfanityException(String message, Throwable cause) {
        super(message, cause);
    }
}