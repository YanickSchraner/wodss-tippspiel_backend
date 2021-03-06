package ch.fhnw.wodss.tippspiel.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RESTExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RESTExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(ResourceNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Not Found");
        response.setErrorMessage(ex.getMessage());

        logger.info("Request for an inexisting resource: " + ex.getMessage());
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalActionException.class)
    public ResponseEntity<ExceptionResponse> illegalAction(IllegalActionException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Illegal Action");
        response.setErrorMessage(ex.getMessage());

        logger.warn("Request tried to perform an illegal action: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ResourceNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> notAllowed(ResourceNotAllowedException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Not Allowed");
        response.setErrorMessage(ex.getMessage());

        logger.warn("User tried to access a resource which doesn't belong to himself: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> alreadyExists(ResourceAlreadyExistsException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Duplicate");
        response.setErrorMessage(ex.getMessage());

        logger.warn("User tried to persist a duplicate: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
