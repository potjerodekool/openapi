package org.platonos.rest.gen

import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
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
}