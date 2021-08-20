package org.platonos.rest.gen.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.generator.Filer
import org.platonos.rest.gen.openapi.resolver.IdProperty
import java.io.File

interface ApisGenerator {

    fun generateApis(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        sourceDir: File,
        filer: Filer,
        idSchemas: Map<String, IdProperty>
    )
}