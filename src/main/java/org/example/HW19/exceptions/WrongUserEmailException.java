package org.example.HW19.exceptions;

public class WrongUserEmailException extends RuntimeException {

    public WrongUserEmailException(String message) {
        super(message);
    }
}
