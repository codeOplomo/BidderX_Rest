package org.anas.bidderx_rest.exceptions;

public class CredentialsAlreadyExistException extends RuntimeException {
    public CredentialsAlreadyExistException(String message) {
        super(message);
    }
}
