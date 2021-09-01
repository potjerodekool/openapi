package org.platonos.rest.generate.mapper

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.TypeDeclaration

class MapperGenerator {

    fun generate(models: MutableList<CompilationUnit>,
                 entities: MutableList<CompilationUnit>) {

        val entityBeanInfo = entities.map {
            Introspector.getBeanInfo(it.types.first.get())
        }

        models.forEach { modelUnit ->
            val model = modelUnit.types.first.get()

            val modelBeanInfo = Introspector.getBeanInfo(model)

            entities.forEach { entityUnit ->
                val entity = entityUnit.types.first.get()
            }
        }

        /*
        val userDtoBeanInfo = Introspector.getBeanInfo(userPatchDtoCu.types.first.get())
        val userBeanInfo = Introspector.getBeanInfo(userCu.types.first.get())

        val builder = MapperBuilder()
        builder.build(userDtoBeanInfo, userBeanInfo)
        val mapper = builder.getCompilationUnit()
        */
    }

    private fun canMap(model: BeanInfo, entity: BeanInfo) {
        model.properties.values.forEach { modelProperty ->
            val entityProperty = entity.properties[modelProperty.propertyName]

            if (entityProperty != null) {

            }
        }
    }
}