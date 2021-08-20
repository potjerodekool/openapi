package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.*
import org.platonos.rest.gen.openapi.api.HttpMethod

interface OpenApiVisitor {

    fun visitOpenApi(openApi: OpenApi3) {
        openApi.schemas.forEach { (name, schema) ->
            visitSchema(name, schema)
        }

        openApi.paths.forEach { (requestPath, path) ->
            visitPath(requestPath, path)
        }
    }

    fun visitSchema(name: String?, schema: Schema) {
    }

    fun visitPath(requestPath: String, path: Path) {
        if (path.post != null) {
            visitOperation(HttpMethod.POST, requestPath, path.post)
        }

        if (path.get != null) {
            visitOperation(HttpMethod.GET, requestPath, path.get)
        }

        if (path.put != null) {
            visitOperation(HttpMethod.PUT, requestPath, path.put)
        }

        if (path.patch != null) {
            visitOperation(HttpMethod.PATCH, requestPath, path.patch)
        }

        if (path.delete != null) {
            visitOperation(HttpMethod.DELETE, requestPath, path.delete)
        }
    }

    fun visitOperation(method: HttpMethod,
                       requestPath: String,
                       operation: Operation) {
        visitRequestBody(method, requestPath, operation, operation.requestBody)

        operation.responses.forEach { (responseCode, response) ->
            visitResponse(method, requestPath, operation, responseCode, response)
        }
    }

    fun visitRequestBody(method: HttpMethod,
                         requestPath: String,
                         operation: Operation,
                         requestBody: RequestBody) {
    }

    fun visitResponse(method: HttpMethod,
                      requestPath: String,
                      operation: Operation,
                      responseCode: String,
                      response: Response) {
    }
}