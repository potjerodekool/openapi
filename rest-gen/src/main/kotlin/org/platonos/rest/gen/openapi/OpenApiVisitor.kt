package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.*
import org.platonos.rest.gen.openapi.api.HttpMethod

interface OpenApiVisitor {

    fun visitOpenApi(openApi: OpenApi3) {
        openApi.schemas.forEach { (name, schema) ->
            visitSchema(name, schema)
        }

        openApi.paths.forEach { (pathValue, path) ->
            visitPath(pathValue, path)
        }
    }

    fun visitSchema(name: String?, schema: Schema) {
    }

    fun visitPath(pathValue: String, path: Path) {
        visitOperation(HttpMethod.POST, path.post)
        visitOperation(HttpMethod.GET, path.get)
        visitOperation(HttpMethod.PUT, path.put)
        visitOperation(HttpMethod.PATCH, path.patch)
        visitOperation(HttpMethod.DELETE, path.delete)

    }

    fun visitOperation(method: HttpMethod, operation: Operation?) {
        if (operation == null) {
            return
        }

        visitRequestBody(method, operation, operation.requestBody)

        operation.responses.forEach { responseCode, response ->
            visitResponse(method, operation, responseCode, response)
        }
    }

    fun visitRequestBody(method: HttpMethod, operation: Operation, requestBody: RequestBody) {
    }

    fun visitResponse(method: HttpMethod, operation: Operation, responseCode: String, response: Response) {
    }
}