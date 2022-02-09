package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.type.DeclaredType

class AnnotationBuilder {

    var type: DeclaredType? = null
    val attributes = mutableListOf<Attribute>()

    fun withType(typeName: String): AnnotationBuilder {
        return withType(DeclaredType(typeName))
    }

    fun withType(type: DeclaredType): AnnotationBuilder {
        this.type = type
        return this
    }

    fun withAttribute(name: String,
                      attributeValue: AttributeValue): AnnotationBuilder {
        return withAttribute(Attribute.of(name, attributeValue))
    }

    fun withAttribute(name: String,
                      value: String): AnnotationBuilder {
        return withAttribute(Attribute.of(name, value))
    }

    fun withAttribute(name: String,
                      value: Int): AnnotationBuilder {
        return withAttribute(Attribute.of(name, value))
    }

    fun withAttribute(name: String,
                      value: Boolean): AnnotationBuilder {
        return withAttribute(Attribute.of(name, value))
    }

    fun withAttribute(name: String, values: Array<*>): AnnotationBuilder {
        val valueList = values.map { AttributeValue.create(it as Any) }.toList()
        return withAttribute(name, ArrayAttributeValue(valueList))
    }

    fun withAttribute(name: String, values: List<*>): AnnotationBuilder {
        val valueList = values.map { AttributeValue.create(it as Any) }
        return withAttribute(name, ArrayAttributeValue(valueList))
    }

    fun withAttribute(attribute: Attribute): AnnotationBuilder {
        validateAttribute(attribute)
        attributes += attribute
        return this
    }

    fun withValue(value: String): AnnotationBuilder {
        return withAttribute(Attribute.of("value", value))
    }

    fun build(): Annotation {
        return Annotation(type!!, attributes)
    }

    private fun validateAttribute(attribute: Attribute) {
        if (attributes.firstOrNull { it.name == attribute.name } != null) {
            throw Exception("attribute ${attribute.name} allready defined")
        }
    }
}