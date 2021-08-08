package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.Attribute
import org.platonos.rest.gen.type.DeclaredType

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