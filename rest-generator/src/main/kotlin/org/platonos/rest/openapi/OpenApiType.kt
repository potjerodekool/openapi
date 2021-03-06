package org.platonos.rest.openapi

import java.util.*

enum class OpenApiType(val type: String) {

    NUMBER("number"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    STRING("string"),
    ARRAY("array"),
    OBJECT("object"),
    DATE("date"),
    DATE_TIME("date-time");

    companion object {

        fun fromType(type: String): OpenApiType {
            return Arrays.stream(values())
                .filter { it.type == type }
                .findFirst()
                .orElseThrow{ OpenApiException("Invalid openapi type $type")}
        }

        fun isTypeOpenApiType(type: String): Boolean {
            return Arrays.stream(values())
                .anyMatch { it.type == type }
        }
    }
}