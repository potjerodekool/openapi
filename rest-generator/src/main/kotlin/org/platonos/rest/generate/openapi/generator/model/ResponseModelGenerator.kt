package org.platonos.rest.generate.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Response
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.builder.Builders.typeElement
import org.platonos.rest.generate.openapi.DefaultModelNamingStrategy
import org.platonos.rest.generate.openapi.OpenApiVisitor
import org.platonos.rest.generate.openapi.api.ContentType
import org.platonos.rest.generate.openapi.getCreatingRef

class ResponseModelGenerator : OpenApiVisitor {

    private val namingStrategy = DefaultModelNamingStrategy()
    private val typeElementBuilder = typeElement()

    fun process(response: Response) {
        val contentMediaType = response.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

        if (contentMediaType != null) {
            val schema = contentMediaType.schema
            val modelName = namingStrategy.createModelName(schema)
            typeElementBuilder.withSimpleName(modelName)
            visitSchema(null, schema)
        }
    }

    override fun visitSchema(name: String?, schema: Schema) {
        val requiredFields = schema.requiredFields

        schema.properties.forEach { (propertyName, propertySchema) ->
            visitProperty(propertyName,propertySchema, requiredFields.contains(propertyName))
        }
    }

    private fun visitProperty(
        propertyName: String,
        propertySchema: Schema,
        isRequired: Boolean
    ) {

    }
}
