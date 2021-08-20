package org.platonos.rest.gen.pact

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.openapi.OpenApiType
import org.platonos.rest.gen.openapi.api.ContentType
import org.platonos.rest.gen.openapi.api.HttpMethod
import org.platonos.rest.gen.openapi.isPathVariable
import org.platonos.rest.gen.pact.model.*
import org.platonos.rest.gen.util.Functions.replaceFirstChar
import java.lang.StringBuilder
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class PactGenerator(controllerKey: String, private val schemaIdResolver: SchemaIdResolver) {

    private val provider = controllerKey + "Service"
    val pactName = createPactName(controllerKey)

    private val paths = mutableMapOf<String, Path>()
    private var pathsProcessed = false

    private val interactions = mutableListOf<Interaction>()

    private fun createPactName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() }
    }

    fun visitPath(requestPath: String, path: Path) {
        paths[requestPath] = path
    }

    private fun handleOperation(method: HttpMethod, requestPath: String, operation: Operation) {
        val description = operation.summary

        val requestContentMediaType = operation.requestBody.contentMediaTypes[ContentType.APPLICATION_JSON.descriptor]

        val requestBody = if (requestContentMediaType != null) {
            createRequestBodyFromSchema(requestContentMediaType.schema)
        } else {
            emptyMap()
        }

        val request = Request(
            method.name,
            fillPathElements(requestPath),
            requestBody
        )

        val operationResponseEntry = operation.responses.entries
            .firstOrNull { is2XX(it.key) }

        if (operationResponseEntry != null) {
            val statusCode = operationResponseEntry.key
            val headers = mutableMapOf<String, Any?>()

            if (isCreated(statusCode)) {
                headers["location"] = createLocation(requestPath)
            }

            val contentMediaType = operationResponseEntry.value.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
            val body: Any?

            if (contentMediaType != null) {
                body = createResponseBodyFromSchema(contentMediaType.schema)
            } else {
                body = null
            }

            val response = Response(status = statusCode, headers = headers, body = body)

            this.interactions += Interaction(
                description,
                request,
                response
            )
        }
    }

    private fun fillPathElements(requestPath: String): String {
        val sj = StringJoiner("/")
        val withoutIds = StringJoiner("/")

        val parts = requestPath.split("/")

        parts.forEach { part ->
            if (part.startsWith("{")) {
                val subPath = withoutIds.toString()

                val idInfo = schemaIdResolver.getIdInfo(subPath)

                if (idInfo != null) {
                    val idValue = createPropertyValue(idInfo.schema).toString()
                    sj.add(idValue)
                }
            } else {
                sj.add(part)
                withoutIds.add(part)
            }
        }

        return sj.toString()
    }

    private fun createResponseBodyFromSchema(schema: Schema): Any {
        if (schema.type == "array") {
            val map = createResponseBodyFromSchema(schema.itemsSchema) as Map<String, Any?>
            return listOf(map)
        } else {
            val body = mutableMapOf<String, Any?>()

            schema.properties
                .forEach { (propertyName, propertySchema) ->
                    body[propertyName] = createPropertyValue(propertySchema)
                }

            schema.allOfSchemas.forEach { inheritSchema ->
                val map =  createResponseBodyFromSchema(inheritSchema) as Map<String, Any?>
                body += map
            }

            return body
        }
    }

    private fun is2XX(statusCode: String): Boolean {
        return statusCode.startsWith("2")
    }

    private fun isCreated(statusCode: String): Boolean {
        return statusCode == "201"
    }

    private fun createLocation(requestPath: String): String {
        val sj = StringJoiner("/")
        val parts = requestPath.split("/")

        parts.forEach { part ->
            if (isPathVariable(part)) {
                val subPath = sj.toString()
                val idInfo = schemaIdResolver.getIdInfo(subPath)

                if (idInfo != null) {
                    sj.add(createPropertyValue(idInfo.schema).toString())
                }
            } else {
                sj.add(part)
            }
        }

        val replaceRequestPath = sj.toString()

        val sb = StringBuilder()
        sb.append("http://localhost:8080")
        sb.append(replaceRequestPath)
        if (requestPath.endsWith("/").not()) {
            sb.append("/")
        }
        sb.append("1")
        return sb.toString()
    }

    private fun createRequestBodyFromSchema(schema: Schema): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()

        schema.properties.forEach { (propertyName, propertySchema) ->
            map[propertyName] = createPropertyValue(
                propertySchema
            )
        }

        return map
    }

    private fun createPropertyValue(propertySchema: Schema): Any? {
        val openApiType = OpenApiType.fromType(propertySchema.type)
        val format = propertySchema.format
        return when(openApiType) {
            OpenApiType.STRING -> {
                if (format != null) {
                    when(format) {
                        "date" -> LocalDate.of(1970, 1, 1)
                        else -> TODO("format $format")
                    }
                } else {
                    "test"
                }
            }
            OpenApiType.INTEGER -> {
                when(format) {
                    "int64" -> 1L
                    else -> 1
                }
            }
            OpenApiType.ARRAY -> {
                return createResponseBodyFromSchema(propertySchema)
            }
            else -> {
                TODO("type " + propertySchema.type)
            }
        }
    }

    private fun processPaths() {
        if (pathsProcessed) {
            return
        }

        paths.forEach { (requestPath, path) ->
           doProcessPath(requestPath, path)
        }

        pathsProcessed = true
    }

    private fun doProcessPath(requestPath: String, path: Path) {
        if (path.post != null) {
            handleOperation(HttpMethod.POST, requestPath, path.post)
        }

        if (path.get != null) {
            handleOperation(HttpMethod.GET, requestPath, path.get)
        }

        if (path.put != null) {
            handleOperation(HttpMethod.PUT, requestPath, path.put)
        }

        if (path.patch != null) {
            handleOperation(HttpMethod.PATCH, requestPath, path.patch)
        }

        if (path.delete != null) {
            handleOperation(HttpMethod.DELETE, requestPath, path.delete)
        }
    }

    fun createPact(): Pact {
        processPaths()
        return Pact(
            Provider(provider),
            interactions
        )
    }

}