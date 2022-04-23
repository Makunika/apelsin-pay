package ru.pshiblo.common.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.pshiblo.common.exception.ApelsinException;
import ru.pshiblo.common.exception.IntegrationException;


import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleAnyExceptions(Exception e, WebRequest request) {
        log.error("Error: " + e.getMessage() + " request url: " + request.getContextPath(), e);
        return handleExceptionInternal(
                e,
                new CustomResponseBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal service error"),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    @ExceptionHandler(value = {IntegrationException.class})
    public ResponseEntity<?> handleIntegrationExceptions(IntegrationException e, WebRequest request) throws JsonProcessingException {
        return handleExceptionInternal(
                e,
                objectMapper.readTree(e.getMessage()),
                new HttpHeaders(),
                HttpStatus.valueOf(e.getStatus()),
                request
        );
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<?> handleNoSuchElementExceptions(NoSuchElementException e, WebRequest request) {
        return handleExceptionInternal(
                e,
                e.getMessage(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleAnyExceptions(IllegalArgumentException e, WebRequest request) {

        return handleExceptionInternal(
                e,
                new CustomResponseBody(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<?> handleAnyExceptions(ResponseStatusException e, WebRequest request) {
        return handleExceptionInternal(
                e,
                new CustomResponseBody(e.getRawStatusCode(), e.getMessage()),
                new HttpHeaders(),
                e.getStatus(),
                request
        );
    }

    @ExceptionHandler(value = {ApelsinException.class})
    public ResponseEntity<?> handleApelsinException(ApelsinException e, WebRequest request) {
        String message = e.getMessage();
        HttpStatus httpStatus = e.getHttpStatus();

        Class<? extends ApelsinException> clazz = e.getClass();
        ResponseStatus responseStatus = clazz.getDeclaredAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            httpStatus = responseStatus.value();
        }

        return handleExceptionInternal(e, new CustomResponseBody(httpStatus.value(), message),
                new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> handleAccessDeniedException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationResponseBody error = new ValidationResponseBody(status.value(), "Validation error");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new ViolationResponse(fieldError.getField(), fieldError.getDefaultMessage(), Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString()));
        }
        return handleExceptionInternal(ex, error, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationResponseBody error = new ValidationResponseBody(status.value(), "Validation error");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new ViolationResponse(fieldError.getField(), fieldError.getDefaultMessage(), Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString()));
        }
        return handleExceptionInternal(ex, error, headers, status, request);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintValidationException(ConstraintViolationException e, WebRequest request) {
        ValidationResponseBody error = new ValidationResponseBody(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getViolations().add(new ViolationResponse(violation.getPropertyPath().toString(), violation.getMessage(), Optional.ofNullable(violation.getInvalidValue()).orElse("null").toString()));
        }
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new CustomResponseBody(status.value(), ex.getMessage()), headers, status, request);
    }

    @Getter
    @ResponseBody
    @RequiredArgsConstructor
    static class CustomResponseBody {
        private final int status;
        private final String message;
    }

    @Getter
    @ResponseBody
    @RequiredArgsConstructor
    static class ValidationResponseBody {
        private final List<ViolationResponse> violations = new ArrayList<>();
        private final int status;
        private final String message;
    }

    @Getter
    @ResponseBody
    @RequiredArgsConstructor
    static class ViolationResponse {
        private final String fieldName;
        private final String message;
        private final String rejectedValue;
    }
}
