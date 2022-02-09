package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.openapi.api.HttpMethod

interface ModelNamingStrategy {

    @Deprecated("")
    fun createModelName(schema: Schema): String {
        TODO()
    }

    @Deprecated("")
    fun createModelName(method: HttpMethod,
                        isRequest: Boolean,
                        schema: Schema): String {
        TODO()
    }

    @Deprecated("")
    fun createPatchModelName(schema: Schema): String {
        TODO()
    }

    fun createRequestModelName(method: HttpMethod, schema: Schema): String {
        TODO()
    }

    fun createResponseModelName(schema: Schema): String {
        TODO()
    }
}