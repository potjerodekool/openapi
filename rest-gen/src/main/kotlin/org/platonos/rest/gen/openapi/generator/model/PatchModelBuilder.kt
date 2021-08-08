package org.platonos.rest.gen.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.Modifier
import org.platonos.rest.gen.element.Operator
import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.MethodInvocation
import org.platonos.rest.gen.expression.NameExpression
import org.platonos.rest.gen.expression.OperatorExpression
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
        return super.buildModel(createPatchModelName(modelName), schema)
    }

    private fun createPatchModelName(modelName: String): String {
        return modelName + "PatchRequest"
    }

    override fun getTypeDescriptor(schema: Schema): String {
        val typeDescriptor = super.getTypeDescriptor(schema)
        return createPatchModelName(typeDescriptor)
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

    override fun visitProperty(propertyName: String, schema: Schema, param: Any?): Any? {
        val fieldType = createType(schema)

        val fieldBuilder = Builders.field()
            .withSimpleName(propertyName)
            .withType(fieldType)
            .withModifier(Modifier.PRIVATE)
            .withAnnotation(
                Annotation(DeclaredType("com.fasterxml.jackson.annotation.JsonProperty")).withValue(propertyName)
            )

        val methodInvocation = MethodInvocation(
            NameExpression("org.openapitools.jackson.nullable.JsonNullable"),
            "undefined"
        )

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
            .withSimpleName(name)

        setterBuilder.withParameter()
            .withSimpleName(name)
            .withType(type)
            .withModifier(Modifier.FINAL)
            .build()

        val body = BlockStatement(
            ExpressionStatement(
                OperatorExpression(
                    FieldAccess(NameExpression("this"), NameExpression(name)),
                    Operator.ASSING,
                    NameExpression(name)
                )
            ),
            ReturnStatement(NameExpression("this"))
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