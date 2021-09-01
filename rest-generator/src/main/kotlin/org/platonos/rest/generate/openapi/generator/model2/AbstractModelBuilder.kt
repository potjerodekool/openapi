package org.platonos.rest.generate.openapi.generator.model2

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MemberValuePair
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.expr.NormalAnnotationExpr
import com.github.javaparser.ast.type.ReferenceType
import com.github.javaparser.ast.type.Type
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.openapi.OpenApiFormat
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.UnitSchemaVisitor

abstract class AbstractModelBuilder(protected val config: OpenApiGeneratorConfiguration)
    : UnitSchemaVisitor<Any?>
{
    private val compilationUnit = CompilationUnit(
        config.modelPackageName
    )
    private val classDeclaration = ClassOrInterfaceDeclaration()

    fun buildModel(modelName: String,
                   modelSchema: Schema) {
        classDeclaration.setName(modelName)
        compilationUnit.addType(classDeclaration)

        visitSchema(modelSchema, null)
    }

    override fun visitSchema(schema: Schema, param: Any?) {
        visitProperties(schema, schema)
        visitAllOfSchemas(schema)
    }

    private fun visitAllOfSchemas(schema: Schema): Any? {
        schema.allOfSchemas.forEach { otherSchema ->
            visitProperties(otherSchema, schema)
        }
        return null
    }

    private fun visitProperties(schema: Schema, rootSchema: Schema): Any? {
        schema.properties.forEach { (name, propertySchema) ->
            visitProperty(name, propertySchema, rootSchema)
        }

        schema.properties.forEach { (name, propertySchema) ->
            generateMethodsForProperty(name, propertySchema, schema, rootSchema)
        }

        return null
    }

    abstract fun generateMethodsForProperty(name: String?,
                                            propertySchema: Schema?,
                                            schema: Schema, rootSchema: Schema)

    override fun visitProperty(propertyName: String, schema: Schema, param: Any?) {
        val type = createType(schema)

        val field = classDeclaration.addField(type, propertyName, Modifier.Keyword.PRIVATE)
        val jsonPropertyAnnotation =
            field.addAndGetAnnotation("com.fasterxml.jackson.annotation.JsonProperty")
        jsonPropertyAnnotation.addPair("value", propertyName)

        if (schema.format != null) {
            val formatAnnotation = createFormatAnnotation(schema.format)
            //field.addAnnotation()
        }

        TODO("Not yet implemented")
    }

    private fun createFormatAnnotation(format: String): Any {
        val openApiFormat = OpenApiFormat.fromApiName(format)

        when(openApiFormat) {
            OpenApiFormat.DATE -> {
                MemberValuePair()
            }
        }

        /*
        NormalAnnotationExpr(
            Name("com.fasterxml.jackson.annotation.JsonFormat")
        )
        */

        TODO("Not yet implemented")
    }

    open fun createType(schema: Schema): Type {
        TODO()
    }
}