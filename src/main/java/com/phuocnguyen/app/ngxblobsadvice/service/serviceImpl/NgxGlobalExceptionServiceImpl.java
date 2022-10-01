package com.phuocnguyen.app.ngxblobsadvice.service.serviceImpl;

import com.ngxsivaos.config.EventListenerConfig;
import com.phuocnguyen.app.ngxblobsadvice.service.NgxGlobalExceptionService;
import com.sivaos.Model.Response.Extend.HttpStatusCodesResponseDTO;
import com.sivaos.Model.Response.Extend.StatusCodeResponseDTO;
import com.sivaos.Model.Response.Original.ApiErrorsGlobalResponse;
import com.sivaos.Model.Response.Original.ApiErrorsResponse;
import com.sivaos.Model.Response.Original.ApiValidationErrorsSimpleResponse;
import com.sivaos.Model.Response.Original.EntityNotFoundExceptionResponse;
import com.sivaos.Utility.CollectionsUtility;
import com.sivaos.Utils.LoggerUtils;
import com.sivaos.Utils.ObjectUtils;
import com.sivaos.Utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service(value = "ngxGlobalExceptionService")
public class NgxGlobalExceptionServiceImpl implements NgxGlobalExceptionService {

    private static final Logger logger = LoggerFactory.getLogger(NgxGlobalExceptionServiceImpl.class);

    @Override
    public ResponseEntity<Object> buildApiEntityResponse(ApiErrorsGlobalResponse apiErrors) {
        return new ResponseEntity<>(apiErrors, apiErrors.getStatus());
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException missingServletRequestParameterException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = missingServletRequestParameterException.getRequestPartName().concat(" parameter is missing");
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST, error, request.getSessionId(), missingServletRequestParameterException);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param missingServletRequestParameterException MissingServletRequestParameterException
     * @param headers                                 HttpHeaders
     * @param status                                  HttpStatus
     * @param request                                 WebRequest
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException missingServletRequestParameterException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = missingServletRequestParameterException.getParameterName().concat(" parameter is missing");
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST, error, request.getSessionId(), missingServletRequestParameterException);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(mediaType -> builder.append(mediaType).append(", "));
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.METHOD_NOT_ALLOWED, builder.substring(0, builder.length() - 2), request.getSessionId(), ex);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.METHOD_NOT_ALLOWED.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.METHOD_NOT_ALLOWED);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(mediaType -> builder.append(mediaType).append(", "));
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), request.getSessionId(), ex);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.UNSUPPORTED_MEDIA_TYPE.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.UNSUPPORTED_MEDIA_TYPE);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param methodArgumentNotValidException the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers                         HttpHeaders
     * @param status                          HttpStatus
     * @param request                         WebRequest
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST);
        // apiErrorsGlobalResponse.addValidationError(methodArgumentNotValidException.getBindingResult().getAllErrors());
        apiErrorsGlobalResponse.addValidationErrors(methodArgumentNotValidException.getBindingResult().getFieldErrors());
        apiErrorsGlobalResponse.setDebugMessage(methodArgumentNotValidException.toString());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());

        String rawErrors = LoggerUtils.toJson(apiErrorsGlobalResponse.getSubErrors());
        List<ApiValidationErrorsSimpleResponse> fieldErrors = Arrays.asList(Objects.requireNonNull(LoggerUtils.parseStrToObs(rawErrors, ApiValidationErrorsSimpleResponse[].class)));

        String message = String.format("%s: %s", "Fields error",
                CollectionsUtility.toString(fieldErrors.stream()
                                .map(ApiValidationErrorsSimpleResponse::getForms)
                                .collect(Collectors.toList()),
                        ","));

        apiErrorsGlobalResponse.setMessage(message);

        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param constraintViolationException the ConstraintViolationException
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException constraintViolationException) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(BAD_REQUEST);
        apiErrorsGlobalResponse.setMessage("Invalid fields request");
        apiErrorsGlobalResponse.addValidationErrors(constraintViolationException.getConstraintViolations());
        apiErrorsGlobalResponse.setDebugMessage(constraintViolationException.toString());
        apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param entityNotFoundExceptionResponse the EntityNotFoundException
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundExceptionResponse entityNotFoundExceptionResponse) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.NOT_FOUND);
        apiErrorsGlobalResponse.setMessage(entityNotFoundExceptionResponse.getMessage());
        apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.NOTFOUND.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.NOTFOUND);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param httpMessageNotReadableException HttpMessageNotReadableException
     * @param headers                         HttpHeaders
     * @param status                          HttpStatus
     * @param request                         WebRequest
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException httpMessageNotReadableException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        logger.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        String error = "Malformed JSON request";

        Throwable mostSpecificCause = httpMessageNotReadableException.getMostSpecificCause();

        if (ObjectUtils.allNotNull(mostSpecificCause)) {
            String exceptionName = mostSpecificCause.getClass().getName();
            String message = mostSpecificCause.getMessage();
            error = String.format("%s => %s", exceptionName, message);
        }

        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST, error, request.getSessionId(), httpMessageNotReadableException);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param httpMessageNotWritableException HttpMessageNotWritableException
     * @param headers                         HttpHeaders
     * @param status                          HttpStatus
     * @param request                         WebRequest
     * @return the ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException httpMessageNotWritableException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error writing JSON output", request.getSessionId(), httpMessageNotWritableException);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }


    /**
     * Handle NoHandlerFoundException.
     *
     * @param noHandlerFoundException -
     * @param headers                 -
     * @param status                  -
     * @param request                 -
     * @return the #ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException noHandlerFoundException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST);
        apiErrorsGlobalResponse.setMessage(String.format("Could not find the %s method for URL %s", noHandlerFoundException.getHttpMethod(), noHandlerFoundException.getRequestURL()));
        apiErrorsGlobalResponse.setDebugMessage(noHandlerFoundException.getMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle javax.persistence.EntityNotFoundException
     *
     * @return the #ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException entityNotFoundException) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.NOT_FOUND, entityNotFoundException);
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.NOTFOUND.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.NOTFOUND);
        apiErrorsGlobalResponse.setMessage("Entity not found");
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param dataIntegrityViolationException the DataIntegrityViolationException
     * @param request                         -
     * @return the #ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException dataIntegrityViolationException, WebRequest request) {
        if (dataIntegrityViolationException.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.CONFLICT, dataIntegrityViolationException.getMostSpecificCause().getMessage(), dataIntegrityViolationException.getCause()); // message: Database error contrains
            apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.CONFLICT.getCode());
            apiErrorsGlobalResponse.setDebugMessage(dataIntegrityViolationException.getMostSpecificCause().getMessage());
            apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
            apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.CONFLICT);
            apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
            return buildApiEntityResponse(apiErrorsGlobalResponse);
        }
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.INTERNAL_SERVER_ERROR, dataIntegrityViolationException);
        apiErrorsGlobalResponse.setDebugMessage(dataIntegrityViolationException.getMostSpecificCause().getMessage());
        apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR.getCode());
        apiErrorsGlobalResponse.setMessage(dataIntegrityViolationException.getMessage());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param methodArgumentTypeMismatchException the Exception
     * @return the #ApiErrorsGlobalResponse object
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(BAD_REQUEST);
        apiErrorsGlobalResponse.setMessage(
                String.format("The parameter '%s' of value '%s' could not be converted to type '%s'. Note, set '%s' is '%s' data type",
                        methodArgumentTypeMismatchException.getName(),
                        methodArgumentTypeMismatchException.getValue(),
                        Objects.requireNonNull(methodArgumentTypeMismatchException.getRequiredType()).getSimpleName(),
                        methodArgumentTypeMismatchException.getName(),
                        Objects.requireNonNull(methodArgumentTypeMismatchException.getRequiredType()).getSimpleName()
                )
        );
        apiErrorsGlobalResponse.setDebugMessage(methodArgumentTypeMismatchException.getMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException exception) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        StatusCodeResponseDTO statusCodeResponse = HttpStatusCodesResponseDTO.BAD_REQUEST;
        statusCodeResponse.setText(exception.getLocalizedMessage());
        ApiErrorsResponse apiErrorsResponse = new ApiErrorsResponse(errors);
        apiErrorsResponse.setCode(statusCodeResponse.getCode());
        apiErrorsResponse.setDescription(statusCodeResponse.getDescription());
        apiErrorsResponse.setText(statusCodeResponse.getText());
        apiErrorsResponse.setType(statusCodeResponse.getType());
        return new ResponseEntity<>(apiErrorsResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.FORBIDDEN);
        apiErrorsGlobalResponse.setMessage("Access denied");
        apiErrorsGlobalResponse.setDebugMessage(ex.getMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.FORBIDDEN.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.FORBIDDEN);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return new ResponseEntity<>(apiErrorsGlobalResponse, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.CONFLICT);
        String message = String.format("%s:%s", ex.getMessage(), "There was a conflict in the application :(");
        apiErrorsGlobalResponse.setMessage(message);
        apiErrorsGlobalResponse.setDebugMessage(ex.getMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.CONFLICT.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.CONFLICT);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        String message = String.format("%s:%s", ex.getMessage(), "There was a logic error in the application :(");
        apiErrorsGlobalResponse.setMessage(message);
        apiErrorsGlobalResponse.setDebugMessage(ex.getMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.INTERNAL_SERVER_ERROR);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<String> errors = new ArrayList<>();
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST);
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        logger.info("list bindExceptions : {}", errors);
        apiErrorsGlobalResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiErrorsGlobalResponse.addValidationError(ex.getBindingResult().getGlobalErrors());
        apiErrorsGlobalResponse.setMessage(ex.getLocalizedMessage());
        apiErrorsGlobalResponse.setSession(request.getSessionId());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.BAD_REQUEST);
        apiErrorsGlobalResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiErrorsGlobalResponse.addValidationError(ex.getBindingResult().getGlobalErrors());
        apiErrorsGlobalResponse.setMessage(ex.getLocalizedMessage());
        apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.BAD_REQUEST.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.BAD_REQUEST);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }

    @Override
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        ApiErrorsGlobalResponse apiErrorsGlobalResponse = new ApiErrorsGlobalResponse(HttpStatus.EXPECTATION_FAILED);
        apiErrorsGlobalResponse.setMessage("File too large!");
        apiErrorsGlobalResponse.setDebugMessage(ex.getLocalizedMessage());
        apiErrorsGlobalResponse.setSession(SessionUtils.snapGenerateSession());
        apiErrorsGlobalResponse.setCode(HttpStatusCodesResponseDTO.EXPECTATION_FAILED.getCode());
        apiErrorsGlobalResponse.setHeader(HttpStatusCodesResponseDTO.EXPECTATION_FAILED);
        apiErrorsGlobalResponse.setPath(EventListenerConfig.getRequest().getServletPath());
        return buildApiEntityResponse(apiErrorsGlobalResponse);
    }
}
