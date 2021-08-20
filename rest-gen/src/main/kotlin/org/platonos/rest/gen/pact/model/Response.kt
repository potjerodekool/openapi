package org.platonos.rest.gen.pact.model

import com.fasterxml.jackson.annotation.JsonInclude

class Response(val status: String,
               val headers: Map<String, Any?>,
               body: Any? = null) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val body: Any? = body
}
