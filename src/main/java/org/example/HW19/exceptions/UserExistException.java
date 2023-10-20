package org.example.HW19.exceptions;

public class UserExistException extends RuntimeException {

    public UserExistException(String message) {
        super(message);
    }
}
