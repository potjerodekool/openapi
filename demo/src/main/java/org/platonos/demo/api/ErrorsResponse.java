package org.platonos.demo.api;

import java.util.List;

public record ErrorsResponse(List<FieldErrorResponse> fieldErrors) {
}
