package org.platonos.rest.generate.openapi

import java.util.*

enum class ParameterLocation(val location: String) {

    PATH("path"),
    QUERY("query"),
    HEADER("header"),
    COOKIE("cookie");

    companion object {

        fun fromLocation(location: String): ParameterLocation {
            return Arrays.stream(values())
                .filter { it.location == location }
                .findFirst()
                .orElseThrow { OpenApiException("invalid parameter location $location") }
        }
    }

}