package org.platonos.rest.openapi.api

class ApiModel {

    private val _properties = mutableMapOf<String, ApiModelProperty>()
    val properties: Map<String, ApiModelProperty> = _properties

    var modelName: String = ""

    fun addProperty(name: String,
                    property: ApiModelProperty) {
        _properties[name] = property
    }

}