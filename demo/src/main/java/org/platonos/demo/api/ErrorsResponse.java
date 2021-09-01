package org.platonos.demo.api;

import java.util.List;

public class ErrorsResponse {

    private final List<FieldErrorResponse> fieldErrors;

    public ErrorsResponse(List<FieldErrorResponse> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }
}
