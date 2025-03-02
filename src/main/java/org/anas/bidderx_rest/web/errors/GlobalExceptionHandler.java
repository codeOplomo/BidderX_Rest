package org.anas.bidderx_rest.web.errors;

import jakarta.validation.ConstraintViolationException;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.service.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessageDTO> handleRuntimeException(RuntimeException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidTokenException(InvalidTokenException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidPasswordException(InvalidPasswordException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsAlreadyExistException.class)
    public ResponseEntity<ResponseMessageDTO> handleCredentialsAlreadyExistException(CredentialsAlreadyExistException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ResponseMessageDTO> handleEmailAlreadyExistException(EmailAlreadyExistException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseMessageDTO> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseMessageDTO> handleUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VerificationCodeException.class)
    public ResponseEntity<ResponseMessageDTO> handleVerificationCodeException(VerificationCodeException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidBidException(InvalidBidException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAuctionException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidAuctionException(InvalidAuctionException exception) {
        return new ResponseEntity<>(new ResponseMessageDTO(exception.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
