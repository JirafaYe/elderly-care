package org.cuit.app.exception;

public class AuthorizedException extends RuntimeException{
    public AuthorizedException(String message) {
        super(message);
    }
}
