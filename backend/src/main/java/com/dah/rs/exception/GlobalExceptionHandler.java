package com.dah.rs.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static void logError(Exception ex, HttpServletRequest request) {
    LOG.error("message: {}", ex.getMessage());
    LOG.error("url: {}", request.getRequestURI());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleException(
    BadRequestException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          BAD_REQUEST.value(),
          "Bad Request"
        ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleException(
    HttpMessageNotReadableException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          BAD_REQUEST.value(),
          "Bad Request"
        ));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleException(
    NotFoundException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(NOT_FOUND)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          NOT_FOUND.value(),
          "Not Found"
        ));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiError> handleException(
    NoSuchElementException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(NOT_FOUND)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          NOT_FOUND.value(),
          "No Such Element"
        ));
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiError> handleException(
    UsernameNotFoundException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(FORBIDDEN)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          FORBIDDEN.value(),
          FORBIDDEN.getReasonPhrase()
        ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleException(
    AccessDeniedException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(FORBIDDEN)
      .body(
        ApiError.of(
          ex.getMessage(),
          request.getRequestURI(),
          FORBIDDEN.value(),
          FORBIDDEN.getReasonPhrase()
        ));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleException(
    MethodArgumentTypeMismatchException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ApiError.of(
          "Invalid value: " + ex.getName(),
          request.getRequestURI(),
          BAD_REQUEST.value(),
          "Method Argument Type Mismatch"
        ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleException(
    MethodArgumentNotValidException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ApiError.of(
          ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(",")),
          request.getRequestURI(),
          BAD_REQUEST.value(),
          "Method Argument Not Valid"
        ));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleException(
    ConstraintViolationException ex,
    HttpServletRequest request
  ) {
    logError(ex, request);
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ApiError.of(
          ex.getMessage().substring(ex.getMessage().indexOf(" ") + 1),
          request.getRequestURI(),
          BAD_REQUEST.value(),
          "Constraint Violation"
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
    logError(ex, request);
    return ResponseEntity
      .status(INTERNAL_SERVER_ERROR)
      .body(
        ApiError.of(
          "An unexpected error occurred.",
          request.getRequestURI(),
          INTERNAL_SERVER_ERROR.value(),
          "Internal Server Error"
        ));
  }

}
