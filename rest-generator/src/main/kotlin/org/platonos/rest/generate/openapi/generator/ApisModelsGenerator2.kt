package org.platonos.rest.generate.openapi.generator

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Operation
import org.platonos.rest.generate.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.generate.openapi.OpenApiVisitor
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.openapi.api.ContentType
import org.platonos.rest.generate.openapi.api.HttpMethod
import org.platonos.rest.generate.openapi.generator.model.DefaultModelBuilder
import org.platonos.rest.generate.openapi.generator.model.SourcePath

class ApisModelsGenerator2(private val platformSupport: PlatformSupport,
                           private val packageName: String,
                           private val sourcePath: SourcePath) : OpenApiVisitor {

    private lateinit var config: OpenApiGeneratorConfiguration

    fun generateModels(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration
    ) {
        this.config = config
        visitOpenApi(openApi)
    }

    override fun visitOperation(method: HttpMethod, requestPath: String, operation: Operation) {
        operation.responses.forEach { (_, response) ->
            val contentMediaType = response.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentMediaType != null) {
                val schema = contentMediaType.schema

                val builder = DefaultModelBuilder(
                    platformSupport,
                    config,
                    sourcePath,
                    true
                )

                val modelNamingStrategy = config.modelNamingStrategy
                val modelName = modelNamingStrategy.createModelName(schema)

                builder.buildModel(modelName, schema)
            }
        }
    }


}