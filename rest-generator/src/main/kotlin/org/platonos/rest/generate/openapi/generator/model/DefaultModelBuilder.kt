package org.platonos.rest.generate.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.element.builder.Builders
import org.platonos.rest.generate.element.builder.Builders.annotation
import org.platonos.rest.generate.element.builder.VariableElementBuilder

import org.platonos.rest.generate.expression.FieldAccess
import org.platonos.rest.generate.expression.IdentifierExpression
import org.platonos.rest.generate.expression.OperatorExpression
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.statement.BlockStatement
import org.platonos.rest.generate.statement.ExpressionStatement
import org.platonos.rest.generate.statement.ReturnStatement
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate.type.TypeKind

open class DefaultModelBuilder(
    platformSupport: PlatformSupport,
    config: OpenApiGeneratorConfiguration,
    sourcePath: SourcePath,
    private val isResponse: Boolean
) : AbstractModelBuilder(platformSupport, config, sourcePath) {


    override fun createType(schema: Schema): Type {
        val typeDescriptor = schema.type

        if (typeConverter.isOpenApiType(typeDescriptor)) {
            return typeConverter.convert(typeDescriptor, schema)
        } else {
            return DeclaredType(getQualifiedClassName(packageName, getTypeDescriptor(schema)))
        }
    }

    override fun visitProperty(propertyName: String, schema: Schema, param: Any?): Any? {
        val type = createType(schema)

        val fieldBuilder = Builders.field()
            .withSimpleName(propertyName)
            .withType(type)
            .withModifier(Modifier.PRIVATE)
            .withAnnotation(
                Annotation(DeclaredType("com.fasterxml.jackson.annotation.JsonProperty")).withValue(propertyName)
            )

        addAdditionalFieldProperties(fieldBuilder, propertyName, param as Schema)

        if (schema.format != null) {
            val formatAnnotation = createFormatAnnotation(schema.format)

            if (formatAnnotation != null) {
                fieldBuilder.withAnnotation(formatAnnotation)
            }
        }

        typeElementBuilder.withEnclosedElement(fieldBuilder.build())
        return null
    }

    private fun addAdditionalFieldProperties(
        fieldBuilder: VariableElementBuilder,
        propertyName: String,
        rootSchema: Schema
    ) {
        if (!isResponse) {
            return
        }

        if (rootSchema.requiredFields.contains(propertyName)) {
            return
        }

        val fieldType = fieldBuilder.type
        if (fieldType.getKind() == TypeKind.DECLARED) {
            val annotation = annotation()
                .withType(
                    DeclaredType("com.fasterxml.jackson.annotation.JsonInclude")
                )
                .withAttribute(
                    Attribute.of("value", EnumAttributeValue(
                        DeclaredType("com.fasterxml.jackson.annotation.JsonInclude.Include"),
                        "NON_NULL"
                    ))
                )
                .build()
            fieldBuilder.withAnnotation(annotation)
        } else if (fieldType.getKind().isPrimitive()) {
            val annotation = annotation()
                .withType(
                    DeclaredType("com.fasterxml.jackson.annotation.JsonInclude")
                )
                .withAttribute(
                    Attribute.of("value", EnumAttributeValue(
                        DeclaredType("com.fasterxml.jackson.annotation.JsonInclude.Include"),
                        "NON_DEFAULT"
                    ))
                )
                .build()
            fieldBuilder.withAnnotation(annotation)
        }
    }

    override fun isRequiredField(schema: Schema, name: String): Boolean {
        return schema.requiredFields.contains(name)
    }

    override fun addBuilderSetter(name: String, type: Type,
                         propertySchema: Schema,
                         schema: Schema) {

        val setterBuilder = Builders.method()
            .withModifier(Modifier.PUBLIC)
            .withSimpleName(name)

        setterBuilder.withParameter()
            .withSimpleName(name)
            .withType(type)
            .withModifier(Modifier.FINAL)
            .build()

        val returnType = createType(schema)

        val body = BlockStatement(
            ExpressionStatement(
                OperatorExpression(
                    FieldAccess(IdentifierExpression("this"), IdentifierExpression(name)),
                    Operator.ASSING,
                    IdentifierExpression(name)
                )
            ),
            ReturnStatement(IdentifierExpression("this"))
        )

        val setter = setterBuilder
            .withBody(body)
            .withReturnType(returnType).build()

        typeElementBuilder.withEnclosedElement(setter)
    }

}