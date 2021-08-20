package org.platonos.rest.gen.openapi.generator.api

import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.generator.Filer

interface ApiGenerator {

    fun init(config: OpenApiGeneratorConfiguration,
             platformSupport: PlatformSupport,
             url: String,
             packageElement: PackageElement,
             filer: Filer
    )
}