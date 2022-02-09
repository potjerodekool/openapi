package org.platonos.rest.openapi.api

import org.platonos.rest.generate.type.Type

class ApiModelProperty(val propertyType: Type,
                       val format: String? = null,
                       val required: Boolean = false,
                       val isNullable: Boolean = false,
                       val isReadOly: Boolean = false,
                       val schema: PropertySchema? = null,
                       val optional: Boolean = false)