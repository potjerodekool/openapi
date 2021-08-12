package org.platonos.rest.gen.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.AnnotationBuilder
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.element.builder.Builders.annotation
import org.platonos.rest.gen.element.builder.Builders.methodInvocation
import org.platonos.rest.gen.expression.*
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ExpressionStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type

class PatchModelBuilder(
    platformSupport: PlatformSupport,
    config: OpenApiGeneratorConfiguration,
    packageName: String,
    sourcePath: SourcePath
) : AbstractModelBuilder(platformSupport, config, packageName, sourcePath) {

    override fun buildModel(modelName: String, schema: Schema): TypeElement {
        val patchModelName = modelNamingStrategy.createPatchModelName(schema)
        return super.buildModel(patchModelName, schema)
    }

    override fun getTypeDescriptor(schema: Schema): String {
        return modelNamingStrategy.createPatchModelName(schema)
    }

    override fun createType(schema: Schema): Type {
        val type = super.createType(schema)

        if (typeConverter.isOpenApiType(schema).not()) {
            if (sourcePath.typeElementExists(type.getQualifiedName()).not()) {
                val simpleName = super.getTypeDescriptor(schema)
                val schemaOfType = sourcePath.getSchema(simpleName)!!

                val newBuilder = PatchModelBuilder(platformSupport, config, packageName, sourcePath)
                newBuilder.buildModel(simpleName, schemaOfType)
            }
        }

        return DeclaredType("org.openapitools.jackson.nullable.JsonNullable",  listOf(type))
    }

    private fun createSchemaAnnotation(schema: Schema): Annotation {
        val schemaAnnotation = annotation()
            .withType(DeclaredType("io.swagger.v3.oas.annotations.media.Schema"))
            .withAttribute(Attribute.of("type", schema.type))

        if (schema.format != null) {
            schemaAnnotation.withAttribute(Attribute.of("format", schema.format))
        }

        return schemaAnnotation.build()
    }

    override fun visitProperty(propertyName: String, schema: Schema, param: Any?): Any? {
        val fieldType = createType(schema)

        val fieldBuilder = Builders.field()
            .withSimpleName(propertyName)
            .withType(fieldType)
            .withModifier(Modifier.PRIVATE)
            .withAnnotation(
                Annotation(DeclaredType("com.fasterxml.jackson.annotation.JsonProperty")).withValue(propertyName)
            ).withAnnotation(createSchemaAnnotation(schema))

        val methodInvocation = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("org.openapitools.jackson.nullable.JsonNullable"),
                    IdentifierExpression("undefined")
                )
            )
            .build()

        fieldBuilder.withValue(methodInvocation)

        if (schema.format != null) {
            val formatAnnotation = createFormatAnnotation(schema.format)

            if (formatAnnotation != null) {
                fieldBuilder.withAnnotation(formatAnnotation)
            }
        }

        typeElementBuilder.withEnclosedElement(fieldBuilder.build())
        return null
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

        val returnType = DeclaredType("${packageName}.${typeElementBuilder.simpleName}")

        val setter = setterBuilder
            .withBody(body)
            .withReturnType(returnType).build()

        typeElementBuilder.withEnclosedElement(setter)
    }

    override fun isRequiredField(schema: Schema, name: String): Boolean {
        return false
    }

}