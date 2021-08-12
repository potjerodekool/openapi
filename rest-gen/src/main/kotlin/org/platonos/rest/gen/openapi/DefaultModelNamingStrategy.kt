package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.util.Functions.replaceFirstChar

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

        val name = modelName.replaceFirstChar { it.uppercaseChar() }
        return "${name}Dto"
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

        val name = modelName.replaceFirstChar { it.uppercaseChar() }
        return "${name}PatchDto"
    }
}