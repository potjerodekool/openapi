package org.platonos.rest.generate.openapi

enum class OpenApiFormat(val fomat: String) {

    DATE("date");

    companion object {

        fun fromApiName(fomat: String): OpenApiFormat {
            val openApiFormat = values()
                .firstOrNull { it.fomat == fomat }
            if (openApiFormat != null) {
                return openApiFormat
            } else {
                TODO(fomat)
            }
        }
    }
}