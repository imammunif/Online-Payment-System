package com.dansmultipro.ops.handler;

import com.dansmultipro.ops.dto.ErrorResponseDto;
import com.dansmultipro.ops.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto<List<String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = ex.getBindingResult().getAllErrors().stream()
                .map((ObjectError oe) -> oe.getDefaultMessage())
                .toList();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleAccessDeniedException(
            AccessDeniedException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ) {
        var errors = "Invalid body request JSON format";
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex
    ) {
        var errors = "Media type is not support";
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleNoResourceFoundException(
            NoResourceFoundException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleNotFoundException(
            NotFoundException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleAlreadyExistsException(
            AlreadyExistsException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyActivatedException.class)
    public ResponseEntity<String> handleAlreadyActivatedException(
            AlreadyActivatedException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InactiveException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInactiveException(
            InactiveException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissMatchException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMissMatchException(
            MissMatchException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleEmailSendException(
            EmailSendException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPageException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidPageException(
            InvalidPageException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidStatusException(
            InvalidStatusException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidIdException(
            InvalidUuidException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleResourceInUseException(
            ResourceInUseException ex
    ) {
        var errors = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.CONFLICT);
    }

}
