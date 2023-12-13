package com.notifier.infrastructure.exception;

public class GoogleCalendarSecurityException extends RuntimeException {

    public GoogleCalendarSecurityException(String message) {
        super(message);
    }

    public GoogleCalendarSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
