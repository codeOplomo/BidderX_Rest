package org.anas.bidderx_rest.exceptions;

public class InvalidProductRequestException extends RuntimeException {
  public InvalidProductRequestException(String message) {
    super(message);
  }
}
