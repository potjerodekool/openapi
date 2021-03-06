package org.platonos.rest.generate.element

object ElementFilter {

    fun fields(elements: List<AbstractElement<*>>): List<VariableElement>  {
        return elements
            .filter { it.kind == ElementKind.FIELD }
            .map { it as VariableElement }
    }

    fun methods(elements: List<AbstractElement<*>>): List<MethodElement>  {
        return elements
            .filter { it.kind == ElementKind.METHOD }
            .map { it as MethodElement }
    }

    fun constructorAndMethods(elements: List<AbstractElement<*>>): List<MethodElement>  {
        return elements
            .filter { it.kind === ElementKind.CONSTRUCTOR || it.kind == ElementKind.METHOD }
            .map { it as MethodElement }
    }

}