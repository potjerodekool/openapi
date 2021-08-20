package org.platonos.rest.gen.openapi

import com.reprezen.jsonoverlay.JsonOverlay
import com.reprezen.jsonoverlay.Reference
import com.reprezen.kaizen.oasparser.model3.Schema
import java.util.*

fun Schema.getCreatingRef(): Reference? {
    val jsonOverlay = this as JsonOverlay<*>
    return jsonOverlay._getCreatingRef()
}

fun createControllerKey(url: String): String {
    val key = StringJoiner("/")
    val parts = url.split("/")
    var index = parts.size - 1
    var stopIndex = -1

    do {
        val part = parts[index]

        if (stopIndex == -1) {
            if (isPathVariable(part).not()) {
                stopIndex = index
            }
        }
        index--
    } while (index > 0)

    index = 0

    do {
        val part = parts[index]

        if (isPathVariable(part).not() && part.isNotEmpty()) {
            key.add(part)
        }
        index++
    } while (index <= stopIndex)

    return key.toString()
}

fun isPathVariable(pathElement: String): Boolean {
    return pathElement.startsWith("{")
}