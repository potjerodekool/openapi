package org.platonos.rest.generate

import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.expression.*
import org.platonos.rest.generate.statement.*
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.NoType
import org.platonos.rest.generate.type.PrimitiveType

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

    fun visitAnnotationAttributeValue(annotationAttributeValue: AnnotationAttributeValue, param: P): R

    fun visitClassAttributeValue(classAttributeValue: ClassAttributeValue, param: P): R

    fun visitEmptyStatement(emptyStatement: EmptyStatement, param: P): R

    fun visitLambaExpression(lambdaExpression: LambdaExpression, param: P): R
}