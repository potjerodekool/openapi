package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.openapi.api.HttpMethod
import org.platonos.rest.generate2.util.Functions.replaceFirstChar

class DefaultModelNamingStrategy : ModelNamingStrategy {

    override fun createModelName(schema: Schema): String {
        val modelName: String

        if (schema.name != null) {
            modelName = schema.name
        } else {
            val createRef = schema.getCreatingRef()

            if (createRef == null) {
                throw OpenApiException("Failed to create model name")
            }

            val refString = createRef.refString
            val nameStart = refString.lastIndexOf('/') + 1
            val nameEnd = refString.lastIndexOf('.')
            modelName = refString.substring(nameStart, nameEnd)
        }

        val name = modelName.replaceFirstChar { it.toUpperCase() }
        return "${name}Dto"
    }

    override fun createModelName(method: HttpMethod,
                        isRequest: Boolean,
                        schema: Schema): String {
        val name = doCreateModelName(schema)
        return when {
            method == HttpMethod.PATCH -> "${name}PatchDto"
            isRequest -> "${name}RequestDto"
            else -> "${name}Dto"
        }
    }

    private fun doCreateModelName(schema: Schema): String {
        val modelName: String

        if (schema.name != null) {
            modelName = schema.name
        } else {
            val createRef = schema.getCreatingRef()

            if (createRef == null) {
                throw OpenApiException("Failed to create model name")
            }

            val refString = createRef.refString
            val nameStart = refString.lastIndexOf('/') + 1
            val nameEnd = refString.lastIndexOf('.')
            modelName = refString.substring(nameStart, nameEnd)
        }

        return modelName.replaceFirstChar { it.toUpperCase() }
    }

    override fun createPatchModelName(schema: Schema): String {
        val modelName: String

        if (schema.name != null) {
            modelName = schema.name
        } else {
            val createRef = schema.getCreatingRef()

            if (createRef == null) {
                throw OpenApiException("Failed to create patch model name")
            }

            val refString = createRef.refString
            val nameStart = refString.lastIndexOf('/') + 1
            val nameEnd = refString.lastIndexOf('.')
            modelName = refString.substring(nameStart, nameEnd)
        }

        val name = modelName.replaceFirstChar { it.toUpperCase() }
        return "${name}PatchDto"
    }

    override fun createRequestModelName(method: HttpMethod, schema: Schema): String {
        val name = createModelClassName(schema)
        return if (method == HttpMethod.PATCH) "${name}PatchDto" else "${name}RequestDto"
    }

    override fun createResponseModelName(schema: Schema): String {
        val name = createModelClassName(schema)

        if (name.endsWith("Response")) {
            return "${name}Dto"
        } else {
            return "${name}ResponseDto"
        }
    }

    private fun createModelClassName(schema: Schema): String {
        val name = schema.getCreatingRef()!!.refString
        val start = name.lastIndexOf('/') + 1
        val end = name.lastIndexOf('.')
        val modelName = name.substring(start, end)
        return modelName.replaceFirstChar { it.toUpperCase() }
    }

    private fun isSharedSchema(schema: Schema): Boolean {
        val isSharedSchema = schema.properties.values
            .any { propertySchema ->
                propertySchema.isReadOnly ||
                        propertySchema.isWriteOnly
            }

        if (isSharedSchema) {
            return true
        }

        return schema.allOfSchemas
            .any { isSharedSchema(it) }
    }
}