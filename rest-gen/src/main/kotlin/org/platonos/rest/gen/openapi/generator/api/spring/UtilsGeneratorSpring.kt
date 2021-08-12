package org.platonos.rest.gen.openapi.generator.api.spring

import org.platonos.rest.gen.openapi.generator.api.UtilsGenerator

class UtilsGeneratorSpring : UtilsGenerator {

    override fun generateUtils(packageName: String): String {
        return String(javaClass.classLoader.getResourceAsStream("utils/ApiUtils.java").readAllBytes())
            .replace("<packageName>", packageName)


    }
}