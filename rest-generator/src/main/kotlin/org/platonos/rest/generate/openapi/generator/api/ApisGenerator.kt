package org.platonos.rest.generate.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.openapi.generator.Filer
import org.platonos.rest.generate.openapi.resolver.IdProperty
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