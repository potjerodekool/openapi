package org.platonos.rest.gen.openapi.generator.api.spring

import org.platonos.rest.gen.openapi.generator.AbstractApisGenerator
import org.platonos.rest.gen.openapi.generator.api.ApiDefinitionGenerator
import org.platonos.rest.gen.openapi.generator.api.ApiImplementationGenerator
import org.platonos.rest.gen.openapi.generator.api.UtilsGenerator

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