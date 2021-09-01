package org.platonos.rest.generate.openapi.generator.api

import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.openapi.generator.Filer

interface ApiGenerator {

    fun init(config: OpenApiGeneratorConfiguration,
             platformSupport: PlatformSupport,
             url: String,
             packageElement: PackageElement,
             filer: Filer
    )
}