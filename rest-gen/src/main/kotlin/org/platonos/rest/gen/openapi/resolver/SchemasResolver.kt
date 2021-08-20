package org.platonos.rest.gen.openapi.resolver

import com.reprezen.kaizen.oasparser.model3.*
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.OpenApiVisitor
import org.platonos.rest.gen.openapi.api.HttpMethod
import org.platonos.rest.gen.openapi.api.ContentType
import java.util.*

class SchemasResolver(config: OpenApiGeneratorConfiguration) : OpenApiVisitor {

    private val schemas = mutableMapOf<String, Schema>()
    private val patchSchemas = mutableMapOf<String, Schema>()
    private val idSchemas = mutableMapOf<String, IdProperty>()

    private val modelNamingStrategy = config.modelNamingStrategy

    fun getSchemas(): Map<String, Schema> {
        return schemas
    }

    fun getPatchSchemas(): Map<String, Schema> {
        return patchSchemas
    }

    fun getIdSchemas(): Map<String, IdProperty> {
        return idSchemas
    }

    override fun visitRequestBody(method: HttpMethod,
                                  requestPath: String,
                                  operation: Operation,
                                  requestBody: RequestBody) {
        super.visitRequestBody(method, requestPath, operation, requestBody)

        val contentContentType = requestBody.contentMediaTypes[ContentType.APPLICATION_JSON.descriptor]

        if (contentContentType != null) {
            val schema = contentContentType.schema

            if (schema != null) {
                val modelName = visitSchemaAndProperties(schema)

                if (modelName != null && method === HttpMethod.PATCH) {
                    patchSchemas[modelName] = schema
                }
            }
        }
    }

    override fun visitResponse(method: HttpMethod,
                               requestPath: String,
                               operation: Operation,
                               responseCode: String,
                               response: Response) {
        super.visitResponse(method, requestPath, operation, responseCode, response)

        val contentContentType = response.contentMediaTypes[ContentType.APPLICATION_JSON.descriptor]

        if (contentContentType != null) {
            val schema = contentContentType.schema

            if (schema != null) {
                visitSchemaAndProperties(schema)
            }

            if (method == HttpMethod.GET) {
                if (schema.allOfSchemas.size == 1 && schema.properties.size == 1) {
                    val properties = schema.properties
                    val keyPropertyName = properties.keys.first()
                    val keySchema = properties.values.first()
                    val idProperty = IdProperty(keyPropertyName, keySchema)

                    val path = stripLastPathVariable(requestPath)

                    idSchemas[path] = idProperty
                }
            }
        }
    }

    private fun stripLastPathVariable(requestPath: String): String {
        val elements = requestPath.split("/")
        val lastElement = elements.last()

        if (lastElement.startsWith("{") && lastElement.endsWith("}")) {
            val sj = StringJoiner("/")

            (0 until elements.size - 1).forEach {
                sj.add(elements[it])
            }

            return sj.toString()
        } else {
            return requestPath
        }
    }

    private fun visitSchemaAndProperties(schema: Schema): String? {
        if (schema.type != "object") {
            return null
        }

        val modelName = modelNamingStrategy.createModelName(schema)

        if (schemas.containsKey(modelName).not()) {
            schemas[modelName] = schema

            schema.properties.values.forEach { propertySchema ->
                visitSchemaAndProperties(propertySchema)
            }
        }

        return modelName
    }
}