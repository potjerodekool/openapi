package org.platonos.rest.openapi

import com.reprezen.jsonoverlay.JsonOverlay
import com.reprezen.jsonoverlay.Reference
import com.reprezen.kaizen.oasparser.model3.Schema

fun Schema.getCreatingRef(): Reference? {
    val jsonOverlay = this as JsonOverlay<*>
    return jsonOverlay._getCreatingRef()
}
