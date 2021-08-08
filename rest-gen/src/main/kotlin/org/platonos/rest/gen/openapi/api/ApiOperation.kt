package org.platonos.rest.gen.openapi.api

class ApiOperation(val requestBody: RequestBody? = null,
                   val responses: Map<Int, RequestBody> = mutableMapOf())