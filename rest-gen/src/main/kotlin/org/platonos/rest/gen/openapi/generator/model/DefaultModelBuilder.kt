package org.platonos.rest.gen.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders

import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.IdentifierExpression
import org.platonos.rest.gen.expression.OperatorExpression
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ExpressionStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type

open class DefaultModelBuilder(
    platformSupport: PlatformSupport,
    config: OpenApiGeneratorConfiguration,
    packageName: String,
    sourcePath: SourcePath
) : AbstractModelBuilder(platformSupport, config, packageName, sourcePath) {


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

        if (schema.format != null) {
            val formatAnnotation = createFormatAnnotation(schema.format)

            if (formatAnnotation != null) {
                fieldBuilder.withAnnotation(formatAnnotation)
            }
        }

        typeElementBuilder.withEnclosedElement(fieldBuilder.build())
        return null
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