package org.example.HW19.exceptions;

public class PasswordNotMatchException extends RuntimeException {

    public PasswordNotMatchException(String message) {
        super(message);
    }
}
