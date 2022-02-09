package org.platonos.rest.generate2

import org.platonos.rest.generate.element.VariableElement
import org.platonos.rest.generate.element.builder.Builders.parameter
import org.platonos.rest.openapi.api.ApiOperation
import org.platonos.rest.openapi.api.ApiParameter
import org.platonos.rest.openapi.api.ContentType
import org.platonos.rest.generate.type.DeclaredType

object ParametersResolver {

    private val useJakartaServlet = ClassPathUtils.useJakartaServlet()
    private val SERVLET_CLASS_NAME = if (useJakartaServlet) "jakarta.servlet.http.HttpServletRequest" else "javax.servlet.http.HttpServletRequest"

    fun resolveParameters(operation: ApiOperation,
                          modelPackageName: String): List<VariableElement> {
        val parameters = mutableListOf<VariableElement>()

        operation.parameters.forEach { apiParameter ->
            parameters += processApiParameter(apiParameter)
        }

        if (operation.requestBody != null) {
            val requestBody = operation.requestBody
            val mediaType = requestBody.contentMediaTypes[ContentType.APPLICATION_JSON.value]

            if (mediaType != null) {
                val modelName = "${modelPackageName}.${mediaType.modelName}"

                val parameterBuilder = parameter()
                    .withType(DeclaredType(modelName))
                    .withSimpleName("model")

                parameters += parameterBuilder.build()
            }
        }

        parameters += parameter()
            .withType(DeclaredType(SERVLET_CLASS_NAME))
            .withSimpleName("request")
            .build()

        return parameters
    }

    private fun processApiParameter(apiParameter: ApiParameter): VariableElement {
        return parameter()
            .withType(apiParameter.type)
            .withSimpleName(apiParameter.name)
            .build()
    }
}