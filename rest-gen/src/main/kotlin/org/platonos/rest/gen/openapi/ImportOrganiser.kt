package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders.methodInvocation
import org.platonos.rest.gen.element.builder.Builders.variableDeclaration
import org.platonos.rest.gen.expression.*
import org.platonos.rest.gen.statement.*
import org.platonos.rest.gen.type.*

class ImportOrganiser : TreeVisitor<Any?,Any> {

    private val imports = mutableListOf<Import>()
    private val importedClasses = mutableSetOf<String>()
    private var packageName: String = ""

    private var compilationUnit: CompilationUnit? = null

    fun getCompilationUnit(): CompilationUnit {
        return compilationUnit!!
    }

    override fun visitCompilationUnit(compilationUnit: CompilationUnit, param: Any?): Any {
        packageName = compilationUnit.packageElement.packageName

        val newTypeElement = compilationUnit.typeElement.accept(this, param) as TypeElement

        this.compilationUnit = CompilationUnit(
            compilationUnit.packageElement,
            newTypeElement,
            imports
        )

        return compilationUnit
    }

    override fun visitPackage(packageElement: PackageElement, param: Any?): Any {
        return packageElement
    }

    override fun visitTypeElement(typeElement: TypeElement, param: Any?): Any {
        val newEnclosingElements = typeElement.enclosedElements
            .map { enclosingElement -> enclosingElement.accept(this, param) }
            .map { it as AbstractElement<*> }
        return typeElement.builder().withoutEnclosedElements()
            .withEnclosedElements(newEnclosingElements)
            .build()
    }

    override fun visitField(field: VariableElement, param: Any?): Any {
        val newAnnotations = field.annotations.map { annotation ->
            annotation.accept(this, param) as Annotation
        }

        val newType = field.type.accept(this, param) as Type
        return field.builder()
            .withoutAnnotations()
            .withAnnotations(newAnnotations)
            .withType(newType)
            .build()
    }

    override fun visitMethod(methodElement: MethodElement, param: Any?): Any {
        val newAnnotations = methodElement.annotations
            .map { annotation -> annotation.accept(this, param) as Annotation }

        val newParameters = methodElement.parameters
            .map { parameter -> parameter.accept(this, param) }
            .map { it as VariableElement }

        val newReturnType = methodElement.returnType.accept(this, param) as Type

        val newBody = methodElement.body.accept(this, param) as Statement

        return methodElement.builder()
            .withoutAnnotations()
            .withAnnotations(newAnnotations)
            .withoutParameters()
            .withParameters(newParameters)
            .withReturnType(newReturnType)
            .withBody(newBody)
            .build()
    }

    override fun visitParameter(parameter: VariableElement, param: Any?): Any {
        val newParamType = parameter.type.accept(this, param) as Type
        return parameter.builder()
            .withType(newParamType)
            .build()
    }

    override fun visitDeclaredType(declaredType: DeclaredType, param: Any?): Any {
        val newTypeArgs = declaredType.typeArgs.map { typeArg -> typeArg.accept(this, param) }
            .map { it as Type }

        if (declaredType.enclosingType != null) {
            val enclosingType = declaredType.enclosingType.accept(this,  param) as DeclaredType
            return DeclaredType(
                declaredType.getQualifiedName(),
                enclosingType,
                newTypeArgs
            )
        } else {
            val qualifiedName = declaredType.getQualifiedName()

            if (qualifiedName.startsWith("java.lang.") || isInSamePackage(qualifiedName)) {
                return DeclaredType(qualifiedToSimpleName(qualifiedName), newTypeArgs)
            } else {
                importClassIfNeeded(qualifiedName)
                return DeclaredType(qualifiedToSimpleName(qualifiedName), newTypeArgs)
            }
        }
    }

    private fun importClassIfNeeded(className: String) {
        if (shouldImport(className)) {
            imports += Import(className)
            importedClasses.add(className)
        }
    }

    private fun qualifiedToSimpleName(qualifiedName: String): String {
        val sepIndex = qualifiedName.lastIndexOf('.')
        return if (sepIndex < 0) qualifiedName else qualifiedName.substring(sepIndex + 1)
    }

    private fun shouldImport(className: String): Boolean {
        if (className.startsWith("java.lang.") || isInSamePackage(className)) {
            return false
        }
        return importedClasses.contains(className).not()
    }

    private fun isInSamePackage(className: String): Boolean {
        return className.startsWith("$packageName.")
    }

    override fun visitNoType(noType: NoType, param: Any?): Any {
        return noType
    }

    override fun visitPrimitiveType(primitiveType: PrimitiveType, param: Any?): Any {
        return primitiveType
    }

    override fun visitAnnotation(annotation: Annotation, param: Any?): Any {
        val newType = annotation.type.accept(this, param) as DeclaredType
        val newAttributes = annotation.attributes.map { attribute ->
            attribute.accept(this, param) as Attribute
        }
        return Annotation(newType, newAttributes)
    }

    override fun visitAttribute(attribute: Attribute, param: Any?): Any {
        val newAttributeValue = attribute.value.accept(this, param) as AttributeValue
        return Attribute.of(attribute.name, newAttributeValue)
    }

    override fun visitConstantAttributeValue(constantAttributeValue: ConstantAttributeValue, param: Any?): Any {
        return constantAttributeValue
    }

    override fun visitEnumAttributeValue(enumAttributeValue: EnumAttributeValue, param: Any?): Any {
        val newType = enumAttributeValue.type.accept(this, param) as DeclaredType
        return EnumAttributeValue(newType, enumAttributeValue.enumConstant)
    }

    override fun visitBlockStatement(blockStatement: BlockStatement, param: Any?): Any {
        val newStatements = blockStatement.statements
            .map { statement -> statement.accept(this, param) as Statement }
        return BlockStatement(newStatements)
    }

    override fun visitExpressionStatement(expressionStatement: ExpressionStatement, param: Any?): Any {
        val newExpression = expressionStatement.expression.accept(this, param) as Expression
        return ExpressionStatement(newExpression)
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, param: Any?): Any {
        val newExpression = returnStatement.expression.accept(this, param) as Expression
        return ReturnStatement(newExpression)
    }

    override fun visitVariableDeclation(variableDeclaration: VariableDeclaration, param: Any?): Any {
        val newInit = variableDeclaration.init?.accept(this, param) as Expression?
        return variableDeclaration()
            .withModifiers(variableDeclaration.modifiers)
            .withType(variableDeclaration.type)
            .withName(variableDeclaration.name)
            .withInit(newInit)
            .build()
    }

    override fun visitFieldAccess(fieldAccess: FieldAccess, param: Any?): Any {
        val newTarget = fieldAccess.target?.accept(this, param) as Expression?
        val newFieldExpression = fieldAccess.fieldExpression.accept(this, param) as Expression
        return FieldAccess(newTarget, newFieldExpression)
    }

    override fun visitIdentifierExpression(identifierExpression: IdentifierExpression, param: Any?): Any {
        if (identifierExpression.type == null) {
            return identifierExpression
        } else {
            val className = identifierExpression.name
            importClassIfNeeded(className)
            val simpleName = qualifiedToSimpleName(className)

            return IdentifierExpression(
                simpleName,
                identifierExpression.type
            )
        }
    }

    override fun visitMethodInvocation(methodInvocation: MethodInvocation, param: Any?): Any {
        val newMethodSelect = methodInvocation.methodSelect?.accept(this, param) as Expression?
        val newTypeArgs = methodInvocation.typeArgs.map { typeArg -> typeArg.accept(this, param) as Type }
        val newParameters = methodInvocation.parameters.map { parameter -> parameter.accept(this, param) as Expression }

        return methodInvocation()
            .withSelect(newMethodSelect)
            .withTypeArgs(newTypeArgs)
            .withParameters(newParameters)
            .build()
    }

    override fun visitOperatorExpression(operatorExpression: OperatorExpression, param: Any?): Any {
        val newLeft = operatorExpression.left.accept(this, param) as Expression
        val newRight = operatorExpression.right.accept(this, param) as Expression
        return OperatorExpression(newLeft, operatorExpression.operator, newRight)
    }

    override fun visitArrayAttributeValue(arrayAttributeValue: ArrayAttributeValue, param: Any?): Any {
        return arrayAttributeValue
    }
}