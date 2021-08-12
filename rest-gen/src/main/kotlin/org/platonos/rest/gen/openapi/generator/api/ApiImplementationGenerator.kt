package org.platonos.rest.gen.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.Path
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.openapi.PlatformSupport

interface ApiImplementationGenerator {

    fun init(config: OpenApiGeneratorConfiguration,
             platformSupport: PlatformSupport,
             url: String,
             packageElement: PackageElement)

    fun generateApiImplementation(url: String, path: Path)

    fun getApiImplementation(): TypeElement
}