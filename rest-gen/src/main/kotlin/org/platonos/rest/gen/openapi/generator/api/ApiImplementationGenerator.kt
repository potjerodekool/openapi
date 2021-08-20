package org.platonos.rest.gen.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.generator.Filer
import org.platonos.rest.gen.openapi.resolver.IdProperty

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