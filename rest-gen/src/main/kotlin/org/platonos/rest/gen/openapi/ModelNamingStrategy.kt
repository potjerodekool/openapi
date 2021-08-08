package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.Schema

interface ModelNamingStrategy {

    fun getModelName(schema: Schema): String?
}