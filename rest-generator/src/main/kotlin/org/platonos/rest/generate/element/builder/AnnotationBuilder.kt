package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.element.Attribute
import org.platonos.rest.generate.type.DeclaredType

class AnnotationBuilder {

    var type: DeclaredType? = null
    val attributes = mutableListOf<Attribute>()

    fun withType(type: DeclaredType): AnnotationBuilder {
        this.type = type
        return this
    }

    fun withAttribute(attribute: Attribute): AnnotationBuilder {
        attributes += attribute
        return this
    }

    fun withValue(value: String): AnnotationBuilder {
        return withAttribute(Attribute.of("value", value))
    }

    fun build(): Annotation {
        return Annotation(type!!, attributes)
    }
}