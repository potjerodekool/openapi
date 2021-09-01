package org.platonos.rest.generate.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.Path
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.openapi.generator.Filer
import org.platonos.rest.generate.openapi.resolver.IdProperty

interface ApiImplementationGenerator {

    fun init(config: OpenApiGeneratorConfiguration,
             platformSupport: PlatformSupport,
             url: String,
             packageElement: PackageElement,
             filer: Filer,
             idSchemas: Map<String, IdProperty>)

    fun generate(url: String, path: Path)

    fun finish()
}