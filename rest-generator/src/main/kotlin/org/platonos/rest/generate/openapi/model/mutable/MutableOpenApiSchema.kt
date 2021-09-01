package org.platonos.rest.generate.openapi.model.mutable

class MutableOpenApiSchema {

    var type: String? = null
    val properties = mutableListOf<MutableOpenApiProperty>()
}