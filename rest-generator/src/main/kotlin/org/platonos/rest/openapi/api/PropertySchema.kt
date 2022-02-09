package org.platonos.rest.openapi.api

class PropertySchema(val readOnly: Boolean,
                     val nullable: Boolean,
                     val format: String? = null,
                     val required: Boolean) {

    var itemsSchema: ApiModel? = null
}