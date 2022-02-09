package org.platonos.rest.generate2.model

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.Type
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.TypeKind
import org.platonos.rest.generate2.ClassPathUtils
import org.platonos.rest.generate2.util.Functions.replaceFirstChar
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.api.ApiModel
import org.platonos.rest.openapi.api.ApiModelProperty
import org.platonos.rest.openapi.api.HttpMethod

class ModelGenerator(val config: OpenApiGeneratorConfiguration,
                     private val isRequest: Boolean,
                     val httpMethod: HttpMethod) {

    private val cu = CompilationUnit()
    private val clazz = ClassOrInterfaceDeclaration()
    private val jakartaValidation = ClassPathUtils.useJakartaValidation()

    private val notNullAnnotationClassName = if (jakartaValidation) "jakarta.validation.constraints.NotNull" else "javax.validation.constraints.NotNull"
    private val validAnnotationClassName = if (jakartaValidation) "jakarta.validation.Valid" else "javax.validation.Valid"

    fun processModel(model: ApiModel): CompilationUnit {
        cu.setPackageDeclaration(
            config.modelPackageName
        )

        clazz.addModifier(Modifier.Keyword.PUBLIC)
        clazz.setName(model.modelName)

        model.properties.forEach { (propertyName, property) ->
            processProperty(propertyName, property)
        }

        cu.addType(clazz)
        return cu
    }

    private fun processProperty(propertyName: String, property: ApiModelProperty) {
        if (property.isReadOly && isRequest) {
            return
        }

        addFieldFor(propertyName, property)

        addGetterFor(propertyName, property)
        addSetterFor(propertyName, property, false)
        addSetterFor(propertyName, property, true)
    }

    private fun createType(propertyType: org.platonos.rest.generate.type.Type): Type {
        return when(propertyType.getKind()) {
            TypeKind.DECLARED -> {
                val declaredType = propertyType as DeclaredType
                val type = ClassOrInterfaceType()
                type.setName(propertyType.getQualifiedName())

                if (declaredType.typeArgs.isNotEmpty()) {
                    val typeArgs = declaredType.typeArgs.map { createType(it) }
                    val typeArgList = NodeList(typeArgs)
                   type.setTypeArguments(typeArgList)
                }
                type
            }
            TypeKind.SHORT -> PrimitiveType.shortType()
            TypeKind.CHAR -> PrimitiveType.charType()
            TypeKind.BOOLEAN -> PrimitiveType.booleanType()
            TypeKind.INT -> PrimitiveType.intType()
            TypeKind.LONG -> PrimitiveType.longType()
            TypeKind.FLOAT -> PrimitiveType.floatType()
            TypeKind.DOUBLE -> PrimitiveType.doubleType()
            else -> TODO()
        }
    }

    private fun addFieldFor(propertyName: String, property: ApiModelProperty) {
        val fieldType = createType(property.propertyType)
        val field = clazz.addField(fieldType, propertyName, Modifier.Keyword.PRIVATE)
        val annot = NormalAnnotationExpr()
        annot.setName("com.fasterxml.jackson.annotation.JsonProperty")
        annot.addPair("value", StringLiteralExpr(propertyName))

        field.addAnnotation(annot)

        val formatAnnotation = createFormatAnnotation(property.format)

        if (formatAnnotation != null) {
            field.addAnnotation(formatAnnotation)
        }

        val schemaAnnotation = createSchemaAnnotation(
            field.variables.first.get().type
        )

        if (schemaAnnotation != null) {
            field.addAnnotation(schemaAnnotation)
        }
    }

    private fun createFormatAnnotation(format: String?): NormalAnnotationExpr? {
        if ("date" == format) {
            val annot = NormalAnnotationExpr()
            annot.setName("com.fasterxml.jackson.annotation.JsonFormat")

            annot.addPair("shape", FieldAccessExpr()
                .setScope(NameExpr("com.fasterxml.jackson.annotation.JsonFormat.Shape"))
                .setName("STRING"))

            annot.addPair("pattern", "yyyy-MM-dd")

            return annot
        }

        return null
    }

    private fun createSchemaAnnotation(type: Type): NormalAnnotationExpr? {
        if (type is ClassOrInterfaceType) {
            val isJsonNullable = type.name.identifier == "org.openapitools.jackson.nullable.JsonNullable"

            val typeArgsOptional = type.typeArguments

            if (typeArgsOptional.isPresent) {
                val typeArgs = typeArgsOptional.get()

                if (typeArgs.isNotEmpty()) {
                    val typeArg = typeArgs.first.get()

                    val schemaAnnotation = NormalAnnotationExpr()
                    schemaAnnotation.setName("io.swagger.v3.oas.annotations.media.Schema")
                    schemaAnnotation.addPair("implementation", ClassExpr(typeArg))

                    if (isList(type)) {
                        val arraySchemaAnnotation = NormalAnnotationExpr()
                        arraySchemaAnnotation.setName("io.swagger.v3.oas.annotations.media.ArraySchema")
                        arraySchemaAnnotation.addPair("schema", schemaAnnotation)
                        return arraySchemaAnnotation
                    } else if (isList(typeArg)) {
                        val subTypeArg = (typeArg as ClassOrInterfaceType).typeArguments.get()
                            .first.get()
                        val nae = NormalAnnotationExpr()
                        nae.setName("io.swagger.v3.oas.annotations.media.Schema")
                        nae.addPair("implementation", ClassExpr(subTypeArg))


                        val arraySchemaAnnotation = NormalAnnotationExpr()
                        arraySchemaAnnotation.setName("io.swagger.v3.oas.annotations.media.ArraySchema")
                        arraySchemaAnnotation.addPair("schema", nae)
                        return arraySchemaAnnotation
                    } else {
                        return schemaAnnotation
                    }
                }
            }
        }

        return null
    }

    private fun isList(type: Type): Boolean {
        if (type.isClassOrInterfaceType.not()) {
            return false
        }

        val classType = type as ClassOrInterfaceType
        return classType.name.identifier == "java.util.List"
    }


    private fun addGetterFor(propertyName: String, property: ApiModelProperty) {
        val getterName = "get${propertyName.replaceFirstChar { it.toUpperCase() }}"
        val getter = clazz.addMethod(getterName, Modifier.Keyword.PUBLIC)
        val type = createType(property.propertyType)
        getter.type = type

        val apiModelPropertyAnnotation = NormalAnnotationExpr()
        apiModelPropertyAnnotation.setName("io.swagger.annotations.ApiModelProperty")

        if (property.required) {
            apiModelPropertyAnnotation.addPair("required", BooleanLiteralExpr(true))
        }

        getter.addAnnotation(apiModelPropertyAnnotation)

        val schemaAnnotation = createSchemaAnnotation(type)

        if (schemaAnnotation != null) {
            getter.addAnnotation(schemaAnnotation)
        }

        if (property.isNullable.not()) {
            getter.addAnnotation(notNullAnnotationClassName)
        }

        if (isRequest && property.format != null) {
            getter.addAnnotation(validAnnotationClassName)
        }

        val body = BlockStmt()
        body.addStatement(ReturnStmt(FieldAccessExpr(ThisExpr(), propertyName)))

        getter.setBody(body)
    }

    private fun addSetterFor(propertyName: String,
                             property: ApiModelProperty,
                             builderSetter: Boolean) {
        val setterName: String =
            if (builderSetter) {
                propertyName
            } else {
                "set${propertyName.replaceFirstChar { it.toUpperCase() }}"
            }

        val setter = clazz.addMethod(setterName, Modifier.Keyword.PUBLIC)

        if (builderSetter) {
            setter.setType(clazz.nameAsString)
        } else {
            setter.setType("void")
        }

        setter.addParameter(
            Parameter()
                .addModifier(Modifier.Keyword.FINAL)
                .setType(createType(property.propertyType))
                .setName(propertyName)
        )

        val statements = NodeList<Statement>()

        statements.add(
            ExpressionStmt(AssignExpr(
                FieldAccessExpr(ThisExpr(), propertyName),
                NameExpr(propertyName),
                AssignExpr.Operator.ASSIGN
            ))
        )

        if (builderSetter) {
            statements.add(ReturnStmt(ThisExpr()))
        }

        setter.setBody(BlockStmt(statements))
    }
}