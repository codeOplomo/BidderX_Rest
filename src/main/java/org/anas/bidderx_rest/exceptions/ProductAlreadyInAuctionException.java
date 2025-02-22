package org.anas.bidderx_rest.exceptions;

public class ProductAlreadyInAuctionException extends RuntimeException {
    public ProductAlreadyInAuctionException(String message) {
        super(message);
    }
}
