package org.platonos.rest.openapi.api

import org.platonos.rest.generate.type.Type

class ApiParameter(
    val name: String,
    val location: ApiParameterLocation,
    val description: String? = null,
    val required: Boolean = false,
    val deprecated: Boolean = false,
    val type: Type
)
