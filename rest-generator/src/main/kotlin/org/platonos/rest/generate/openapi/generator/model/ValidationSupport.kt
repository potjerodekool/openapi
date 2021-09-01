package org.platonos.rest.generate.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.Attribute
import org.platonos.rest.generate.element.builder.Builders.annotation
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.openapi.OpenApiType
import org.platonos.rest.generate.type.DeclaredType

object ValidationSupport {

    fun addValidationApiAnnotations(propertySchema: Schema,
                                            required: Boolean,
                                            getterBuilder: MethodBuilder) {
        addValidAnnotation(propertySchema, getterBuilder)
        addNotNulAnnotation(required, getterBuilder)
        addMinMaxAnnotations(propertySchema, getterBuilder)
        addSizeAnnotation(propertySchema, getterBuilder)
        processEnum(propertySchema, getterBuilder)
    }

    private fun addValidAnnotation(propertySchema: Schema,
                                   getterBuilder: MethodBuilder) {
        if (propertySchema.format != null) {
            getterBuilder.withAnnotation(
                annotation().withType(DeclaredType("javax.validation.Valid")).build()
            )
        }
    }

    private fun addNotNulAnnotation(required: Boolean,
                                    getterBuilder: MethodBuilder) {
        if (required) {
            getterBuilder.withAnnotation(
                annotation()
                    .withType(DeclaredType("javax.validation.constraints.NotNull"))
                    .build()
            )
        }
    }

    private fun addMinMaxAnnotations(propertySchema: Schema,
                                     getterBuilder: MethodBuilder) {
        val min = propertySchema.minimum
        val max = propertySchema.maximum

        if (min != null) {
            val annotation = annotation()
                .withType(DeclaredType("javax.validation.constraints.DecimalMin"))
                .withValue(min.toString())

            if (isTrue(propertySchema.exclusiveMinimum)) {
                annotation.withAttribute(Attribute.of("inclusive", false))
            }

            getterBuilder.withAnnotation(annotation.build())
        }

        if (max != null) {
            val annotation = annotation()
                .withType(DeclaredType("javax.validation.constraints.DecimalMax"))
                .withValue(max.toString())

            if (isTrue(propertySchema.exclusiveMaximum)) {
                annotation.withAttribute(Attribute.of("inclusive", false))
            }

            getterBuilder.withAnnotation(annotation.build())
        }
    }

    private fun addSizeAnnotation(propertySchema: Schema, getterBuilder: MethodBuilder) {
        if (propertySchema.minLength != null || propertySchema.maxLength != null) {

            val annotation = annotation()
                .withType(DeclaredType("javax.validation.constraints.Size"))
                .withAttribute(Attribute.of("min", propertySchema.minLength ?: 0))
                .withAttribute(Attribute.of("max", propertySchema.minLength ?: Int.MAX_VALUE))
                .build()

            getterBuilder.withAnnotation(annotation)
        }
    }

    private fun processEnum(propertySchema: Schema, getterBuilder: MethodBuilder) {
        if (propertySchema.hasEnums()) {
            val openApiType = OpenApiType.fromType(propertySchema.type)

            when (openApiType) {
                OpenApiType.BOOLEAN -> processBooleanEnum(propertySchema, getterBuilder)
                OpenApiType.STRING -> processStringEnum(propertySchema, getterBuilder)
                else -> { /*Ignore other types*/ }
            }
        }
    }

    private fun processBooleanEnum(propertySchema: Schema, getterBuilder: MethodBuilder) {
        var assertFalse: Boolean? = null
        var assertTrue: Boolean? = null

        propertySchema.enums.forEach { enum ->
            if (enum == true) {
                assertTrue = true
            } else if (enum == false) {
                assertFalse = true
            }
        }

        if (assertFalse == true && assertTrue == null) {
            val annotation = annotation()
                .withType(DeclaredType("javax.validation.constraints.AssertFalse"))
                .build()
            getterBuilder.withAnnotation(annotation)
        } else if (assertTrue == true && assertFalse == null) {
            val annotation = annotation()
                .withType(DeclaredType("javax.validation.constraints.AssertTrue"))
                .build()
            getterBuilder.withAnnotation(annotation)
        }
    }

    private fun processStringEnum(propertySchema: Schema, getterBuilder: MethodBuilder) {
        val regexp = propertySchema.enums.joinToString(separator = "|")

        val annotation = annotation().withType(DeclaredType("org.plantonos.validation.EnumNamePattern"))
            .withAttribute(Attribute.of("regexp", regexp))
            .build()

        getterBuilder.withAnnotation(annotation)
    }

    private fun isTrue(b: Boolean?): Boolean {
        return b != null && b == true
    }
}