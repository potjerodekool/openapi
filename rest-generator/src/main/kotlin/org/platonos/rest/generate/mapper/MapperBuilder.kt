package org.platonos.rest.generate.mapper

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.resolution.types.ResolvedType
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl

class MapperBuilder {

    private val cu = CompilationUnit()

    fun getCompilationUnit(): CompilationUnit {
        return cu
    }

    fun build(source: BeanInfo, target: BeanInfo) {
        val td = ClassOrInterfaceDeclaration()
        td.addModifier(Modifier.Keyword.PUBLIC)
        td.addModifier(Modifier.Keyword.FINAL)
        td.setName("${source.name}And${target.name}Mapper")
        cu.types += td

        doBuild(source, target, td)
        doBuild(target, source, td)
    }

    private fun doBuild(source: BeanInfo, target: BeanInfo,
                        typeDeclaration: ClassOrInterfaceDeclaration) {
        val statements = mutableListOf<Statement>()

        val sourceProperties = source.properties

        target.properties.values
            .filter { property -> property.setter != null }
            .forEach { property ->
                val sourceProperty = sourceProperties[property.propertyName]

                if (sourceProperty?.getter != null) {
                    val setter = property.setter!!
                    val getter = sourceProperty.getter

                    val fromType = getter.type.resolve()
                    val toType = setter.parameters.first.get().type.resolve()

                    if (isAssignable(fromType, toType)) {
                        statements += ExpressionStmt(MethodCallExpr(NameExpr("target"), setter.nameAsString, NodeList.nodeList(MethodCallExpr(NameExpr("source"), getter.nameAsString))))
                    } else if (isJsonNullable(fromType)) {
                        val fromReferenceType = fromType as ResolvedReferenceType
                        val typeParam = fromReferenceType.typeParametersValues().first()

                        if (isAssignable(typeParam, toType)) {
                            val condition: Expression = MethodCallExpr(NameExpr("source"), "isPresent")

                            statements += IfStmt(
                                condition,
                                BlockStmt()
                                    .addStatement(ExpressionStmt(MethodCallExpr(NameExpr("target"), setter.nameAsString, NodeList.nodeList(MethodCallExpr(NameExpr("source"), "get"))))),
                                null
                            )
                        }

                    }
                }
        }

        if (statements.isNotEmpty()) {
            val method = typeDeclaration.addMethod("to", Modifier.Keyword.PUBLIC, Modifier.Keyword.FINAL)

            method.type = ClassOrInterfaceType()
                .setName(target.name)

            method.parameters += Parameter()
                .addModifier(Modifier.Keyword.FINAL)
                .setName("source")
                .setType(source.name)

            method.parameters += Parameter()
                .addModifier(Modifier.Keyword.FINAL)
                .setName("target")
                .setType(target.name)

            val body = method.createBody()

            statements.forEach { statement ->
                body.addStatement(statement)
            }
            body.addStatement(ReturnStmt(NameExpr("target")))
        }
    }

    private fun isAssignable(from: ResolvedType, to: ResolvedType): Boolean {
        if (from.isAssignableBy(to)) {
            return true
        }
        return false
    }

    private fun isJsonNullable(type: ResolvedType): Boolean {
        if (type is ResolvedReferenceType) {
            val referenceType = type as ReferenceTypeImpl
            return "org.openapitools.jackson.nullable.JsonNullable" == referenceType.qualifiedName
        } else {
            return false
        }
    }
}