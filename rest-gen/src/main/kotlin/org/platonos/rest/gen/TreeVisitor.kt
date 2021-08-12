package org.platonos.rest.gen

import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.IdentifierExpression
import org.platonos.rest.gen.expression.MethodInvocation
import org.platonos.rest.gen.expression.OperatorExpression
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ExpressionStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.statement.VariableDeclaration
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.NoType
import org.platonos.rest.gen.type.PrimitiveType

interface TreeVisitor<P,R> {

    fun visitCompilationUnit(compilationUnit: CompilationUnit, param: P): R

    fun visitPackage(packageElement: PackageElement, param: P): R

    fun visitTypeElement(typeElement: TypeElement, param: P): R

    fun visitField(field: VariableElement, param: P): R

    fun visitMethod(methodElement: MethodElement, param: P): R

    fun visitParameter(parameter: VariableElement, param: P): R

    fun visitDeclaredType(declaredType: DeclaredType, param: P): R

    fun visitNoType(noType: NoType, param: P): R

    fun visitPrimitiveType(primitiveType: PrimitiveType, param: P): R

    fun visitAnnotation(annotation: Annotation, param: P): R

    fun visitAttribute(attribute: Attribute, param: P): R

    fun visitConstantAttributeValue(constantAttributeValue: ConstantAttributeValue, param: P): R

    fun visitEnumAttributeValue(enumAttributeValue: EnumAttributeValue, param: P): R

    fun visitBlockStatement(blockStatement: BlockStatement, param: P): R

    fun visitExpressionStatement(expressionStatement: ExpressionStatement, param: P): R

    fun visitReturnStatement(returnStatement: ReturnStatement, param: P): R

    fun visitVariableDeclation(variableDeclaration: VariableDeclaration, param: P): R

    fun visitFieldAccess(fieldAccess: FieldAccess, param: P): R

    fun visitIdentifierExpression(identifierExpression: IdentifierExpression, param: P): R

    fun visitMethodInvocation(methodInvocation: MethodInvocation, param: P): R

    fun visitOperatorExpression(operatorExpression: OperatorExpression, param: P): R

    fun visitArrayAttributeValue(arrayAttributeValue: ArrayAttributeValue, param: P): R
}