package com.phuocnguyen.app.ngxblobsadvice.service;

import com.sivaos.Model.Response.Original.ApiErrorsGlobalResponse;
import com.sivaos.Model.Response.Original.EntityNotFoundExceptionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;

public interface NgxGlobalExceptionService {

    ResponseEntity<Object> buildApiEntityResponse(ApiErrorsGlobalResponse apiErrorsGlobalResponse);

    ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException missingServletRequestParameterException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException constraintViolationException);

    ResponseEntity<Object> handleEntityNotFound(EntityNotFoundExceptionResponse entityNotFoundExceptionResponse);

    ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException httpMessageNotReadableException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException httpMessageNotWritableException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException noHandlerFoundException, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException entityNotFoundException);

    ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException dataIntegrityViolationException, WebRequest request);

    ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException, WebRequest request);

    ResponseEntity<?> constraintViolationException(ConstraintViolationException exception);

    ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request);

    ResponseEntity<Object> handleConflict(RuntimeException ex, final WebRequest request);

    ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request);

    ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request);

    ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex);

    ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException maxUploadSizeExceededException);
}
