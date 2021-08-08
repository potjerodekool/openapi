package org.platonos.rest.gen.patch

import org.platonos.rest.gen.Templates
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.model.Model

class PatcherGenerator {

    fun generate(
        model: Model,
        patchRequestType: DeclaredType,
        entityType: DeclaredType,
        patchMappings: List<SinglePatchMapping>): String {
        val st = Templates.getInstanceOf("patcher/patcher")!!

        val patcherPackageName = if (model.hasPackageName) model.packageName + ".patcher" else ""
        val patcherSimpleName = entityType.getSimpleName() + "Patcher"

        st.add("model", model)
        st.add("patchMappings", patchMappings)

        if (model.hasPackageName) {
            st.add("patcherPackageName", model.packageName + ".patcher")
        }

        st.add("patcherSimpleName", patcherSimpleName)
        st.add("patchRequestType", patchRequestType)
        st.add("entityType", entityType)
        return st.render()
    }
}