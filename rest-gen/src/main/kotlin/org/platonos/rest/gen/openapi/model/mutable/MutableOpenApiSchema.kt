package org.platonos.rest.gen.openapi.model.mutable

class MutableOpenApiSchema {

    var type: String? = null
    val properties = mutableListOf<MutableOpenApiProperty>()
}