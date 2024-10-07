package git.dimitrikvirik.springsoft.common.config;


import git.dimitrikvirik.springsoft.common.model.dto.ErrorDTO;
import git.dimitrikvirik.springsoft.common.services.ConstraintList;
import io.jsonwebtoken.ExpiredJwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private ConstraintList constraintList;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getMessage())
                .timestamp(new Date()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getMessage())
                .timestamp(new Date()).build(),
                HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getReason())
                .timestamp(new Date()).build(),
                ex.getStatusCode());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getMessage())
                .timestamp(new Date()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message("Token expired")
                .timestamp(new Date()).build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorDTO.builder().timestamp(new Date()).message(String.format("Validation failed: %s", errors)).build()
        );
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String error = "A database error occurred. Please try again.";

        if (ex.getCause() instanceof ConstraintViolationException) {
            String constraintName = ((ConstraintViolationException) ex.getCause()).getConstraintName();
            if (constraintName != null && constraintList != null) {
                for (Map.Entry<String, String> constraint : constraintList.getConstraintList().entrySet()) {
                    if (constraint.getKey().equals(constraintName)) {
                        error = constraint.getValue();
                    }
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ErrorDTO.builder().timestamp(new Date()).message(error).build()
                );
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorDTO.builder().timestamp(new Date()).message(error).build()
        );
    }


    @Autowired(required = false)
    public void setConstraintList(ConstraintList constraintList) {
        this.constraintList = constraintList;
    }
}