package org.platonos.rest.generate.mapper

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.type.VoidType
import java.util.stream.Collectors

object Introspector {

    fun getBeanInfo(typeDeclaration: TypeDeclaration<*>): BeanInfo {
        val properties = resolveProperties(typeDeclaration)
        return BeanInfo(typeDeclaration.nameAsString, properties)
    }

    private fun resolveProperties(typeDeclaration: TypeDeclaration<*>): Map<String, Property> {
        val propertyInfoMap = mutableMapOf<String, MutableProperty>()

        typeDeclaration.methods.forEach { methodDeclaration ->
            if (methodDeclaration.isGetter()) {
                val propertyName = methodDeclaration.getPropertyName()!!
                val type = methodDeclaration.type

                var mutableProperty = propertyInfoMap[propertyName]

                if (mutableProperty != null) {
                    if (mutableProperty.type.isSameType(type).not()) {
                        throw BeanException("getter has other type then getter $propertyName")
                    }
                    mutableProperty.getter = methodDeclaration
                } else {
                    mutableProperty = MutableProperty(type)
                    mutableProperty.getter = methodDeclaration
                    propertyInfoMap[propertyName] = mutableProperty
                }
            } else if (methodDeclaration.isSetter()) {
                val propertyName = methodDeclaration.getPropertyName()!!

                val type = methodDeclaration.parameters.first.get().type

                var mutableProperty = propertyInfoMap[propertyName]

                if (mutableProperty != null) {
                    if (mutableProperty.type.isSameType(type).not()) {
                        throw BeanException("setter has other type then getter $propertyName")
                    }

                    mutableProperty.setter = methodDeclaration
                } else {
                    mutableProperty = MutableProperty(type)
                    mutableProperty.setter = methodDeclaration
                    propertyInfoMap[propertyName] = mutableProperty
                }
            }
        }

        return propertyInfoMap.entries.stream()
            .map {
                val value = it.value
                it.key to Property(it.key, value.type.resolve(), value.getter, value.setter)
            }.collect(
                Collectors.toMap(
                    { it.first },
                    { it.second }
                )
            ).toMap()
    }
}

fun MethodDeclaration.isGetter(): Boolean {
    if (nameAsString.startsWith("get").not()) {
        return false
    }

    if (type is VoidType) {
        return false
    }

    return parameters.isEmpty()
}

fun MethodDeclaration.isSetter(): Boolean {
    if (nameAsString.startsWith("set").not()) {
        return false
    }

    if (type !is VoidType) {
        return false
    }

    return parameters.size == 1
}

fun MethodDeclaration.getPropertyName(): String? {
    return if (isGetter() || isSetter()) {
        nameAsString.substring(3).replaceFirstChar { it.lowercase() }
    } else {
        null
    }
}

class MutableProperty(val type: Type) {
    var getter: MethodDeclaration? = null
    var setter: MethodDeclaration? = null
}

fun Type.isSameType(otherType: Type): Boolean {
    if (javaClass != otherType.javaClass) {
        return false
    } else {
        return when(javaClass) {
            ClassOrInterfaceType::class.java -> isSameType(this as ClassOrInterfaceType, otherType as ClassOrInterfaceType)
            else -> false
        }
    }
}

fun isSameType(a: ClassOrInterfaceType, b: ClassOrInterfaceType): Boolean {
    return a.name.equals(b.name)
}