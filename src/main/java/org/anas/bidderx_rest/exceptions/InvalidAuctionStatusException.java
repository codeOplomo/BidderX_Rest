package org.anas.bidderx_rest.exceptions;

public class InvalidAuctionStatusException extends RuntimeException {
    public InvalidAuctionStatusException(String message) {
        super(message);
    }
}
