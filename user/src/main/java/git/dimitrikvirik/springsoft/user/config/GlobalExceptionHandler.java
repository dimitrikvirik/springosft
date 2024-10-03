package git.dimitrikvirik.springsoft.user.config;

import git.dimitrikvirik.springsoft.user.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getMessage())
                .timestamp(new Date()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ErrorDTO.builder()
                .message(ex.getMessage())
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

}