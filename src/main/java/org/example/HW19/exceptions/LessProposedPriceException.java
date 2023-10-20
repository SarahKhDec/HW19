package org.example.HW19.exceptions;

public class LessProposedPriceException extends RuntimeException{
    public LessProposedPriceException (String message){
        super(message);
    }
}
