package org.platonos.rest.generate2.model

import org.platonos.rest.backend.Filer
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.api.*

class ModelsGenerator(private val config: OpenApiGeneratorConfiguration,
                      private val filer: Filer) {

    private val processedModels = mutableSetOf<String>()

    fun generate(api: Api) {

        api.requestModels.values.forEach { model ->
            processModel(model, true, HttpMethod.POST)
        }

        api.responseModels.values.forEach { model ->
            processModel(model, false, HttpMethod.GET)
        }
    }

    private fun processModel(model: ApiModel,
                             isRequest: Boolean,
                             httpMethod: HttpMethod) {
        val modelName = model.modelName

        if (processedModels.contains(modelName)) {
            return
        }

        val modelGenerator = ModelGenerator(config, isRequest, httpMethod)
        val cu = modelGenerator.processModel(model)

        filer.createSource(
            config.modelPackageName,
            modelName,
            cu.toString()
        )
    }
}