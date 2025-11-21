package org.exi.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildBody(HttpStatus status, String message, WebRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.name());
        body.put("message", message);
        body.put("path", req.getDescription(false));
        return body;
    }

    // 404 - Utente non trovato
    @ExceptionHandler(UtenteNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUtenteNotFound(UtenteNotFoundException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    // 400 - Duplication (codice fiscale già in uso)
    @ExceptionHandler(DuplicationErrorException.class)
    public ResponseEntity<Map<String,Object>> handleDuplication(DuplicationErrorException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    // 406 - Validation errors (DTO non validi)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException e, WebRequest request) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a,b) -> a + "; " + b)   // concatenazione messaggi
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(buildBody(HttpStatus.NOT_ACCEPTABLE, message, request));
    }

    // 401 - Signature error (se mai lo usassi)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String,Object>> handleSignature(SignatureException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildBody(HttpStatus.UNAUTHORIZED, "Token non valido", request));
    }
}