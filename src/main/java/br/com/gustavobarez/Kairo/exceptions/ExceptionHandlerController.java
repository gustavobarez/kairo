package br.com.gustavobarez.Kairo.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {

    public ExceptionHandlerController() {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        List<ErrorMessageDTO> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            String message = err.getDefaultMessage();
            ErrorMessageDTO error = new ErrorMessageDTO(message, err.getField());
            errors.add(error);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleResourceAlreadyExists(ResourceAlreadyExistsException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(e.getMessage(), e.getField()));
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleResourceNotFound(ResourceNotFoundException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(e.getMessage(), null));
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleOperationNotAllowed(OperationNotAllowedException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(e.getMessage(), null));
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleEntityNotFoundException(EntityNotFoundException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(e.getMessage(), null));
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleIllegalArgumentException(IllegalArgumentException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(e.getMessage(), null));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                e.getValue(), e.getName(), e.getRequiredType().getSimpleName());
        errors.add(new ErrorMessageDTO(message, e.getName()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException e) {

        List<ErrorMessageDTO> errors = new ArrayList<>();

        String message = e.getMessage().toLowerCase();
        if (message.contains("unique") || message.contains("duplicate")) {
            if (message.contains("email")) {
                errors.add(new ErrorMessageDTO("Email already exists", "email"));
            } else if (message.contains("username")) {
                errors.add(new ErrorMessageDTO("Username already exists", "username"));
            } else {
                errors.add(new ErrorMessageDTO("This record already exists", null));
            }
        } else {
            errors.add(new ErrorMessageDTO("Data integrity violation", null));
        }

        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleDataAccessException(
            org.springframework.dao.DataAccessException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO("Database error occurred", null));
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleNumberFormatException(NumberFormatException e) {
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO("Invalid number format", null));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleGenericException(Exception e) {
        e.printStackTrace();
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO("Internal server error", null));
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}