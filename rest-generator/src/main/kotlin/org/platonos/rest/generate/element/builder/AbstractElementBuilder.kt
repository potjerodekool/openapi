package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.type.NoType
import org.platonos.rest.generate.type.Type

abstract class AbstractElementBuilder<EB : AbstractElementBuilder<EB>>() {

    var simpleName: String = ""
    var enclosingElement: AbstractElement<*>? = null
    val enclosedElements = mutableListOf<AbstractElement<*>>()
    val annotations = mutableListOf<Annotation>()
    val modifiers = mutableSetOf<Modifier>()
    var type: Type = NoType
    var kind: ElementKind? = null
    val isAbstract: Boolean
    get() { return modifiers.contains(Modifier.ABSTRACT) }

    constructor(element: AbstractElement<*>): this() {
        simpleName = element.simpleName
        enclosingElement = element.enclosingElement
        enclosedElements += element.enclosedElements
        annotations += element.annotations
        modifiers += element.modifiers
        type = element.type
        kind = element.kind
    }

    fun withSimpleName(simpleName: String): EB {
        this.simpleName = simpleName
        return this as EB
    }

    open fun withEnclosingElement(element: AbstractElement<*>): EB {
        this.enclosingElement = element
        return this as EB
    }

    fun withEnclosedElement(element: AbstractElement<*>): EB {
        this.enclosedElements += element
        return this as EB
    }

    fun withEnclosedElements(elements: List<AbstractElement<*>>): EB {
        this.enclosedElements += elements
        return this as EB
    }

    fun withoutEnclosedElements(): EB {
        this.enclosedElements.clear()
        return this as EB
    }

    fun withoutAnnotations(): EB {
        annotations.clear()
        return this as EB
    }

    fun withAnnotation(annotation: Annotation): EB {
        annotations += annotation
        return this as EB
    }

    fun withAnnotations(annotations: List<Annotation>): EB {
        this.annotations += annotations
        return this as EB
    }

    fun withModifier(modifier: Modifier): EB {
        this.modifiers += modifier
        return this as EB
    }

    fun withType(type: Type): EB {
        this.type = type
        return this as EB
    }

    fun withKind(elementKind: ElementKind): EB {
        this.kind = elementKind
        return this as EB
    }

    fun getQualifiedName(): String {
        val packageElement = enclosingElement as QualifiedNameable
        return "${packageElement.getQualifiedName()}.${simpleName}"
    }

    abstract fun build(): AbstractElement<*>

}