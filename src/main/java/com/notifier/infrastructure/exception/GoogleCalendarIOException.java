package com.notifier.infrastructure.exception;

public class GoogleCalendarIOException extends RuntimeException {

    public GoogleCalendarIOException(String message) {
        super(message);
    }

    public GoogleCalendarIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
