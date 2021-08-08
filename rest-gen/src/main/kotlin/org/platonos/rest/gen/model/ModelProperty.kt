package org.platonos.rest.gen.model

import org.platonos.rest.gen.type.Type
import org.platonos.rest.gen.util.Functions.replaceFirstChar

class ModelProperty(val name: String, val type: Type) {

    val getterName: String
    val setterName: String

    init {
        val methodPostFix = name.replaceFirstChar { c -> c.toUpperCase() }
        getterName = "get$methodPostFix"
        setterName = "set$methodPostFix"
    }
}