package org.anas.bidderx_rest.exceptions;

public class InvalidAuctionException extends RuntimeException {
    public InvalidAuctionException(String message) {
        super(message);
    }
}
