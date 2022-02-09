package org.platonos.rest.openapi.api

class ApiOperation(
    val operationId: String?,
    val summary: String?,
    val description: String?,
    val tags: List<String> = emptyList(),
    val requestBody: ApiRequestBody?,
    val responses: Map<String, ApiResponse>,
    val parameters: List<ApiParameter>
)
