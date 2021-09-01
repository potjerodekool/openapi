package org.platonos.demo.api;

import java.util.Objects;

public final class FieldErrorResponse {
    private final String field;
    private final String message;

    public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String field() {
        return field;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FieldErrorResponse) obj;
        return Objects.equals(this.field, that.field) &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message);
    }

    @Override
    public String toString() {
        return "FieldErrorResponse[" +
                "field=" + field + ", " +
                "message=" + message + ']';
    }


}
