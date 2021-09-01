package org.platonos.rest.generate.openapi

import com.reprezen.kaizen.oasparser.model3.Schema

interface ModelNamingStrategy {

    fun createModelName(schema: Schema): String

    fun createPatchModelName(schema: Schema): String
}