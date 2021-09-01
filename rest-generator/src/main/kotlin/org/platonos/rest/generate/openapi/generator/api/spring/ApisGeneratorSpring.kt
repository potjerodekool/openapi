package org.platonos.rest.generate.openapi.generator.api.spring

import org.platonos.rest.generate.openapi.generator.AbstractApisGenerator
import org.platonos.rest.generate.openapi.generator.api.ApiDefinitionGenerator
import org.platonos.rest.generate.openapi.generator.api.ApiImplementationGenerator
import org.platonos.rest.generate.openapi.generator.api.UtilsGenerator

class ApisGeneratorSpring : AbstractApisGenerator() {

    override fun createApiDefinitionGenerator(): ApiDefinitionGenerator {
        return ApiDefinitionGeneratorSpring()
    }

    override fun createApiImplementationGenerator(): ApiImplementationGenerator {
        return ApiImplementationGeneratorSpring()
    }

    override fun createUtilsGenerator(): UtilsGenerator {
        return UtilsGeneratorSpring()
    }
}