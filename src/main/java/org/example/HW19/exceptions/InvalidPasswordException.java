package org.example.HW19.exceptions;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException (String message){
        super(message);
    }
}
