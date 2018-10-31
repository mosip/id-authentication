package org.mosip.demo.exception;

import javax.persistence.EntityNotFoundException;


import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler class
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@ControllerAdvice
public class SchedulingExceptionHandler extends ResponseEntityExceptionHandler {

    public SchedulingExceptionHandler() {
        super();
    }

    // API

    // 400

    /**
     * @param ex The exception
     * @param request The request
     * @return handled exception
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * @param ex The exception
     * @param request The request
     * @return handled exception
     */
    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        // ex.getCause() instanceof JsonMappingException, JsonParseException // for additional information later on
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
    }


    // 404

    /**
     * @param ex The exception
     * @param request The request
     * @return handled exception
     */
    @ExceptionHandler(value = { EntityNotFoundException.class, InvalidRequestException.class })
    protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // 409

    /**
     * @param ex The exception
     * @param request The request
     * @return handled exception
     */
    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 412

    // 500

    /**
     * @param ex The exception
     * @param request The request
     * @return handled exception
     */
    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
    /*500*/public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final String bodyOfResponse = "Invalid Request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}