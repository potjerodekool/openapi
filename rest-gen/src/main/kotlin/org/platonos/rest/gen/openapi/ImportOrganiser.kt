package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.type.*

class ImportOrganiser : TreeVisitor<Any?,Any?> {

    private val imports = mutableListOf<Import>()
    private val importedClasses = mutableSetOf<String>()
    private var packageName: String = ""

    private var compilationUnit: CompilationUnit? = null

    fun getCompilationUnit(): CompilationUnit {
        return compilationUnit!!
    }

    override fun visitCompilationUnit(compilationUnit: CompilationUnit, param: Any?): Any? {
        packageName = compilationUnit.packageElement.packageName

        val newTypeElement = compilationUnit.typeElement.accept(this, param) as TypeElement

        this.compilationUnit = CompilationUnit(
            compilationUnit.packageElement,
            newTypeElement,
            imports
        )

        return null
    }

    override fun visitPackage(packageElement: PackageElement, param: Any?): Any? {
        return null
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

        return methodElement.builder()
            .withoutAnnotations()
            .withAnnotations(newAnnotations)
            .withoutParameters()
            .withParameters(newParameters)
            .withReturnType(newReturnType)
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
                return DeclaredType(getSimpleName(qualifiedName), newTypeArgs)
            } else {
                if (shouldImport(qualifiedName)) {
                    imports += Import(qualifiedName)
                    importedClasses.add(qualifiedName)
                }
                return DeclaredType(getSimpleName(qualifiedName), newTypeArgs)
            }
        }
    }

    private fun getSimpleName(qualifiedName: String): String {
        val sepIndex = qualifiedName.lastIndexOf('.')
        return if (sepIndex < 0) qualifiedName else qualifiedName.substring(sepIndex + 1)
    }

    private fun shouldImport(className: String): Boolean {
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
}