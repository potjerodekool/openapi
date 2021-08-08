package org.platonos.rest.gen.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.element.builder.MethodBuilder
import org.platonos.rest.gen.element.builder.TypeElementBuilder
import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.NameExpression
import org.platonos.rest.gen.expression.OperatorExpression
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.SchemaVisitor
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ExpressionStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type

abstract class AbstractModelBuilder(platformSupport: PlatformSupport,
                                    config: OpenApiGeneratorConfiguration,
                                    protected val packageName: String,
                                    val sourcePath: SourcePath
) : SchemaVisitor<Any?, Any?> {

    protected val platformSupport = platformSupport
    protected val config = config
    protected val typeConverter = platformSupport.getTypeConverter()
    private val modelNamingStrategy = config.modelNamingStrategy

    protected lateinit var typeElementBuilder: TypeElementBuilder

    open fun buildModel(modelName: String,
                   schema: Schema
    ): TypeElement {
        val packageElement = PackageElement(packageName)

        typeElementBuilder = Builders.typeElement()
            .withEnclosingElement(packageElement)
            .withSimpleName(modelName)

        visitModel(schema, null)
        val typeElement = typeElementBuilder.build()
        sourcePath.addTypeElement(typeElement)
        return typeElement
    }

    fun visitProperties(schema: Schema, rootSchema: Schema): Any? {
        schema.properties.forEach { (name, propertySchema) ->
            visitProperty(name, propertySchema, rootSchema)
        }

        schema.properties.forEach { (name, propertySchema) ->
            generateMethodsForProperty(name, propertySchema, schema, rootSchema)
        }

        return null
    }

    open fun createType(schema: Schema): Type {
        val typeDescriptor = schema.type

        if (typeConverter.isOpenApiType(typeDescriptor)) {
            return typeConverter.convert(typeDescriptor, schema)
        } else {
            return DeclaredType(getQualifiedClassName(packageName, getTypeDescriptor(schema)))
        }
    }

    override fun visitModel(schema: Schema, param: Any?): Any? {
        visitProperties(schema, schema)
        visitAllOfSchemas(schema)
        return null
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

    fun createFormatAnnotation(format: String): Annotation? {
        return when(format) {
            "date" -> {
                val shape = EnumAttributeValue(
                    DeclaredType("com.fasterxml.jackson.annotation.JsonFormat")
                        .nested(DeclaredType("Shape")),
                    "STRING"
                )
                return Annotation(DeclaredType("com.fasterxml.jackson.annotation.JsonFormat"))
                    .withAttribute(Attribute.of("shape", shape))
                    .withAttribute(Attribute.of("pattern", "yyyy-MM-dd"))
            }
            else -> null
        }
    }

    fun generateMethodsForProperty(name: String?,
                                   propertySchema: Schema?,
                                   schema: Schema,
                                   rootSchema: Schema
    ) {
        if (name != null && propertySchema != null) {
            val type = createType(propertySchema)
            addGetter(name, type, propertySchema, schema)
            addSetter(name, type, propertySchema, schema)
            addBuilderSetter(name, type, propertySchema, rootSchema)
        }
    }

    fun addGetter(name: String,
                  type: Type,
                  propertySchema: Schema,
                  schema: Schema
    ) {
        val getterName = createMethodName(name, true)

        val getterBuilder = Builders.method()
            .withSimpleName(getterName)
            .withReturnType(type)
            .withBody(ReturnStatement(FieldAccess(NameExpression("this"), NameExpression(name))))

        val required = isRequiredField(schema, name)

        addApiModelPropertyAnnotation(propertySchema, required, getterBuilder)
        addValidationApiAnnotations(propertySchema, required, getterBuilder)

        val getter = getterBuilder.build()

        typeElementBuilder.withEnclosedElement(getter)
    }

    private fun addApiModelPropertyAnnotation(propertySchema: Schema,
                                              required: Boolean,
                                              getterBuilder: MethodBuilder) {
        val apiModelPropertyAnnotationBuilder = Builders.annotation()
            .withType(DeclaredType("io.swagger.annotations.ApiModelProperty"))

        if (required) {
            apiModelPropertyAnnotationBuilder.withAttribute(Attribute.of("required", true))
        }

        if (propertySchema.description != null) {
            apiModelPropertyAnnotationBuilder.withAttribute(Attribute.of("value", propertySchema.description ?: ""))
        }

        getterBuilder.withAnnotation(apiModelPropertyAnnotationBuilder.build())
    }

    private fun addValidationApiAnnotations(propertySchema: Schema,
                                            required: Boolean,
                                            getterBuilder: MethodBuilder) {
        ValidationSupport.addValidationApiAnnotations(propertySchema, required, getterBuilder)


    }

    open fun isRequiredField(schema: Schema, name: String): Boolean {
        return schema.requiredFields.contains(name)
    }

    fun addSetter(name: String, type: Type,
                  propertySchema: Schema,
                  schema: Schema
    ) {
        val setterName = createMethodName(name, false)

        val setterBuilder = Builders.method()
            .withSimpleName(setterName)

        setterBuilder.withParameter()
            .withSimpleName(name)
            .withType(type)
            .withModifier(Modifier.FINAL)
            .build()

        val setter = setterBuilder.withBody(
            ExpressionStatement(
                OperatorExpression(
                    FieldAccess(NameExpression("this"), NameExpression(name)),
                    Operator.ASSING,
                    NameExpression(name)
                )
            )
        ).build()

        typeElementBuilder.withEnclosedElement(setter)
    }

    open fun addBuilderSetter(name: String, type: Type,
                              propertySchema: Schema,
                              schema: Schema
    ) {

        val setterBuilder = Builders.method()
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
                    FieldAccess(NameExpression("this"), NameExpression(name)),
                    Operator.ASSING,
                    NameExpression(name)
                )
            ),
            ReturnStatement(NameExpression("this"))
        )

        val setter = setterBuilder
            .withBody(body)
            .withReturnType(returnType).build()

        typeElementBuilder.withEnclosedElement(setter)
    }

    fun createMethodName(simpleName: String, getter: Boolean): String {
        val prefix = if (getter) "get" else "set"
        val firstChar = simpleName[0].uppercaseChar()

        if (simpleName.length > 1) {
            return prefix + firstChar + simpleName.substring(1)
        } else {
            return prefix + firstChar
        }
    }

    open fun getTypeDescriptor(schema: Schema): String {
        if (schema.type == "object") {
            return modelNamingStrategy.getModelName(schema)!!
        } else {
            return schema.type
        }
    }

    fun visitAllOfSchemas(schema: Schema): Any? {
        schema.allOfSchemas.forEach { otherSchema ->
            visitProperties(otherSchema, schema)
        }
        return null
    }

    fun getQualifiedClassName(packageName: String,
                              simpleName: String): String {
        return if (packageName.isEmpty()) simpleName else "${packageName}.${simpleName}"
    }
}