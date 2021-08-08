package org.platonos.rest.gen.openapi.generator

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.SchemasResolver
import java.io.File

object Parser {

    private val parser = OpenApiParser()

    fun parse(fileName: String): OpenApi3 {
        val file = File(fileName)
        return parser.parse(file) as OpenApi3
    }

    fun resolve(openApi: OpenApi3): SchemasResolver {
        val config = OpenApiGeneratorConfiguration()
        val schemasResolver = SchemasResolver(config)
        schemasResolver.visitOpenApi(openApi)
        return schemasResolver
    }

}