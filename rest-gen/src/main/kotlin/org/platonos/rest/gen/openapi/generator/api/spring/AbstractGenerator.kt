package org.platonos.rest.gen.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.Attribute
import org.platonos.rest.gen.element.Modifier
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.element.builder.Builders.parameter
import org.platonos.rest.gen.element.builder.VariableElementBuilder
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.api.ContentType
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.util.Functions.replaceFirstChar

abstract class AbstractGenerator {

    protected lateinit var modelNamingStrategy: ModelNamingStrategy
    protected lateinit var typeConverter: TypeConverter
    protected lateinit var modelPackageName: String
    protected lateinit var apiPackage: String
    private val dynamicModels = mutableListOf<String>()

    open fun init(config: OpenApiGeneratorConfiguration,
                  platformSupport: PlatformSupport,
                  url: String,
                  packageElement: PackageElement
    ) {
        modelNamingStrategy = config.modelNamingStrategy
        typeConverter = platformSupport.getTypeConverter()
        modelPackageName = config.modelPackageName
        apiPackage = config.apiPackageName
        dynamicModels.addAll(config.dynamicModels)
    }

    fun createApiName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Api"
    }

    fun createBodyParameter(operation: Operation,
                            isPatch: Boolean = false): VariableElementBuilder {
        val requestBody = operation.requestBody

        val bodyRequired = requestBody.required

        val paramAnnotation = Builders.annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"),)

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val schema = getRequestBodySchema(operation)

        val modelName: String
        val bodyType: DeclaredType

        if (schema.getCreatingRef() != null) {
            modelName = if (isPatch) modelNamingStrategy.createPatchModelName(schema) else
                modelNamingStrategy.createModelName(schema)

            if (dynamicModels.contains(modelName).not()) {
                val qualifiedModelName = "${modelPackageName}.$modelName"
                bodyType = DeclaredType(qualifiedModelName)
            } else {
                bodyType = DeclaredType(
                    "java.util.Map",
                    listOf(
                        DeclaredType("java.lang.String"),
                        DeclaredType("java.lang.Object")
                    )
                )
            }
        } else {
            modelName = "body"
            bodyType = DeclaredType(
                "java.util.Map",
                listOf(
                    DeclaredType("java.lang.String"),
                    DeclaredType("java.lang.Object")
                )
            )
        }

        val bodyParamName = modelName.lowercase()

        return parameter()
            .withAnnotation(paramAnnotation.build())
            .withType(bodyType)
            .withSimpleName(bodyParamName)
    }

    fun createHttpServletRequestParameter(): VariableElementBuilder {
        return parameter()
            .withModifier(Modifier.FINAL)
            .withType(DeclaredType("javax.servlet.http.HttpServletRequest"))
            .withSimpleName("httpServletRequest")
    }

    fun getRequestBodySchema(operation: Operation): Schema {
        val contentMediaType = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        return contentMediaType.schema
    }
}