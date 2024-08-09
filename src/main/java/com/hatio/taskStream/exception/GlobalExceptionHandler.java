package com.hatio.taskStream.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        errorDetail.setProperty("errors", errors);
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>("You do not have permission to access this resource. Admin rights are required.", HttpStatus.FORBIDDEN);
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request, HandlerMethod method) {
        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                method.getMethod().getName(),
                ZonedDateTime.now()
        );
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialException.class)
    public ApiErrorResponse handleBadCredentialException(BadCredentialException ex, HttpServletRequest request, HandlerMethod method) {
        return new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI(),
                method.getMethod().getName(),
                ZonedDateTime.now()
        );
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = EmailAlreadyExistsException.class)
    public ApiErrorResponse  handleEmailAlreadyExistException(EmailAlreadyExistsException ex, HttpServletRequest request, HandlerMethod method){
        return new ApiErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI(),
                method.getMethod().getName(),
                ZonedDateTime.now()
        );
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value =Exception.class)
    public ApiErrorResponse  handleException(Exception ex, HttpServletRequest request, HandlerMethod method){
        return new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI(),
                method.getMethod().getName(),
                ZonedDateTime.now()
        );
    }

}
