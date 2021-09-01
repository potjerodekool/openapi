package org.platonos.rest.generate.pact

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Operation
import org.platonos.rest.generate.openapi.api.ContentType
import java.util.*

class SchemaIdResolver {

    private val schemaIdInfoMap = mutableMapOf<String, PropertyInfo>()

    fun visitOpenApi(openApi3: OpenApi3) {
        openApi3.paths.forEach { (requestPath, path) ->
            if (path.get != null) {
                resolveSchemaId(requestPath, path.get)
            }
        }
    }

    private fun resolveSchemaId(requestPath: String, operation: Operation) {
        operation.responses.entries.forEach {
            val contentMediaType = it.value.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentMediaType != null) {
                val schema = contentMediaType.schema

                if (schema.properties.size == 1 && schema.allOfSchemas.size == 1) {
                    val modelSchema = schema.allOfSchemas.first()
                    val entry = schema.properties.entries.first()
                    val idPropertyName = entry.key
                    val propertySchema = entry.value

                    val path = stripPathElements(requestPath)

                    schemaIdInfoMap[path] = PropertyInfo(idPropertyName, propertySchema)
                }
            }
        }
    }

    fun stripPathElements(requestPath: String): String {
        val sj = StringJoiner("/")
        val parts = requestPath.split("/")

        parts.forEach { part ->
            if (part.startsWith("{").not()) {
                sj.add(part)
            }
        }

        return sj.toString()
    }

    fun getIdInfo(path: String): PropertyInfo? {
        return schemaIdInfoMap[path]
    }
}