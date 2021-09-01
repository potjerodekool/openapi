package org.platonos.rest.generate.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.MediaType
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.openapi.OpenApiVisitor
import org.platonos.rest.generate.openapi.api.ContentType
import org.platonos.rest.generate.openapi.api.HttpMethod

class SimpleModelGenerator : OpenApiVisitor {

    override fun visitOperation(method: HttpMethod, requestPath: String, operation: Operation) {
        val requestBody = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

        if (requestBody != null) {
            processRequestBody(requestBody)
        }

        operation.responses
            .forEach { (_, response) ->

            if (response != null) {
                val responseModelGenerator = ResponseModelGenerator()
                responseModelGenerator.process(response)
            }
        }

        super.visitOperation(method, requestPath, operation)
    }

    private fun processRequestBody(requestBody: MediaType) {
    }

}