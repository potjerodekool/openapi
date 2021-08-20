package org.platonos.demo.api;

import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {

        val fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    var field = fieldError.getField();
                    var defaultMessage = fieldError.getDefaultMessage();
                    return new FieldErrorResponse(field, defaultMessage);
                }).collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(new ErrorsResponse(fieldErrors));
    }
}
