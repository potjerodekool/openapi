package org.platonos.rest.openapi.api

class ApiResponse(val description: String = "",
                  val contentMediaTypes: MutableMap<String, ApiMediaType>) {
}