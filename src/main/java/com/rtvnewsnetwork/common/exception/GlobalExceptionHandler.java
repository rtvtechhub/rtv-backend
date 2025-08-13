package com.rtvnewsnetwork.common.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Log log = LogFactory.getLog(GlobalExceptionHandler.class);
    private final String environment;
    private final String maxFileSize;

    @Autowired
    public GlobalExceptionHandler(
            @Value("${spring.profiles.active}") String environment,
            @Value("${spring.servlet.multipart.max-file-size}") String maxFileSize) {
        this.environment = environment;
        this.maxFileSize = maxFileSize;
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleCustomExceptions(
            Exception ex, WebRequest request, HttpServletRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                ex.getLocalizedMessage(), request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseStatusExceptions(
            ResponseStatusException ex, WebRequest request, HttpServletRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                ex.getLocalizedMessage(), request.getDescription(false));
        if (ex.getStatusCode().isError()) {
            log.error(ex.getMessage(), ex);
        }
        return new ResponseEntity<>(exceptionResponse, ex.getStatusCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
            Exception ex, WebRequest request, HttpServletRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                ex.getLocalizedMessage(), request.getDescription(false));
        log.error(exceptionResponse.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "File size is too large, max allowed: " + maxFileSize,
                request.getDescription(false));

        log.error(exceptionResponse.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleIllegalExceptions(
            Exception ex, WebRequest request, HttpServletRequest req) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                ex.getMessage(), request.getDescription(false));
        log.error(exceptionResponse.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ExceptionResponse error = new ExceptionResponse(
                "Validation Failed. " + details,
                request.getDescription(false));

        log.error(error.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ExceptionResponse error = new ExceptionResponse(
                "Validation Failed. " + ex.getMessage(),
                request.getDescription(false));
        log.error(error.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

//	@ExceptionHandler(AccessDeniedException.class)
//	@ResponseStatus(HttpStatus.FORBIDDEN)
//	public final ResponseEntity<ExceptionResponse> handleAccessDeniedExceptions(Exception ex,
//			WebRequest request, HttpServletRequest req) {
//		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),
//				request.getDescription(false));
//
//		log.error(exceptionResponse.getMessage(), ex);
//		return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
//	}

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(
            Exception ex, WebRequest request, HttpServletRequest req) {
        String message = environment.equals("prod")
                ? "Something went wrong"
                : ex.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                message, request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
