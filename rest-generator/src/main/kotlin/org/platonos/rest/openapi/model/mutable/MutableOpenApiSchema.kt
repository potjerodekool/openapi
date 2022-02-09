package org.platonos.rest.openapi.model.mutable

class MutableOpenApiSchema {

    var type: String? = null
    val properties = mutableListOf<MutableOpenApiProperty>()
}