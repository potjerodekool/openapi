package org.platonos.rest.openapi.generator

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import java.io.File

object Parser {

    private val parser = OpenApiParser()

    fun parse(fileName: String): OpenApi3 {
        val file = File(fileName)
        return parser.parse(file) as OpenApi3
    }

}