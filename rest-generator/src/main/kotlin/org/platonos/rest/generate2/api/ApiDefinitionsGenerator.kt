package org.platonos.rest.generate2.api

import org.platonos.rest.backend.Filer
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.Types
import org.platonos.rest.openapi.api.Api
import org.platonos.rest.openapi.api.ApiPath
import org.platonos.rest.openapi.generator.api.spring.UtilsGeneratorSpring
import org.platonos.rest.generate2.util.Functions.replaceFirstChar
import org.platonos.rest.generate2.api.spring.ApiDefinitionGeneratorSpring
import org.platonos.rest.generate2.api.spring.ApiImplementationGeneratorSpring
import java.util.*

class ApiDefinitionsGenerator(val config: OpenApiGeneratorConfiguration,
                              val types: Types,
                              val filer: Filer) {

    private val definitionGenerators = mutableMapOf<String, ApiGenerator>()
    private val implementationGenerators = mutableMapOf<String, ApiGenerator>()
    private val delegateGenerators = mutableMapOf<String, DelegateGenerator>()

    fun generate(api: Api) {
        api.paths.forEach { (url, path) ->
            processPath(url, path)
        }

        definitionGenerators.values.forEach { generator ->
            generator.finish()
        }

        implementationGenerators.values.forEach { generator ->
            generator.finish()
        }

        delegateGenerators.values.forEach { generator ->
            generator.finish()
        }

        if (config.generateApiDefinitions ||
                config.generateApiImplementations) {
            val utilsGenerator = UtilsGeneratorSpring()
            val utilsCode = utilsGenerator.generateUtils(config.apiPackageName)

            filer.createSource(
                config.apiPackageName,
                "ApiUtils",
                utilsCode
            )
        }
    }

    private fun processPath(url: String, path: ApiPath) {
        val controllerKey = getControllerKey(url)
        val controllerName = getControllerName(controllerKey)

        if (config.generateApiDefinitions) {
            processPathForDefinition(url, path, controllerKey, controllerName)
        }

        if (config.generateApiImplementations) {
            processPathForImplementation(url, path,controllerKey, controllerName)
            processPathForDelegator(url, path,controllerKey, controllerName)
        }
    }

    private fun processPathForDefinition(url: String,
                                         path: ApiPath,
                                         controllerKey: String,
                                         controllerName: String) {
        val generator = getDefinitionGenerator(controllerKey, controllerName)
        generator.generate(url, path)
    }

    private fun processPathForImplementation(url: String,
                                             path: ApiPath,
                                             controllerKey: String,
                                             controllerName: String) {
        val generator = getImplementationGenerator(controllerKey, controllerName)
        generator.generate(url, path)
    }

    private fun createDelegateName(controllerName: String): String {
        return controllerName.substring(0, controllerName.length - 3) + "Delegate"
    }

    private fun processPathForDelegator(url: String,
                                        path: ApiPath,
                                        controllerKey: String,
                                        controllerName: String) {
        val delegateName = createDelegateName(controllerName)
        val generator = delegateGenerators.computeIfAbsent(delegateName) {
            DelegateGenerator(delegateName, config, types, filer)
        }
        generator.generate(url, path)

    }

    private fun getDefinitionGenerator(controllerKey: String, controllerName: String): ApiGenerator {
        return definitionGenerators.computeIfAbsent(controllerKey) {
            val generator = ApiDefinitionGeneratorSpring(controllerName, config, types, filer)
            generator.init()
            generator
        }
    }

    private fun getImplementationGenerator(controllerKey: String, controllerName: String): ApiGenerator {
        return implementationGenerators.computeIfAbsent(controllerKey) {
            val generator = ApiImplementationGeneratorSpring(controllerName, createDelegateName(controllerName), config, types, filer)
            generator.init()
            generator
        }
    }

    private fun getControllerKey(url: String): String {
        val elements = url.split("/")
        val controllerKey = StringJoiner("/")

        elements.forEach { element ->
            if (!element.startsWith("{")) {
                controllerKey.add(element)
            }
        }

        return controllerKey.toString()
    }

    private fun getControllerName(controllerKey: String): String {
        val controllerName = StringBuilder()
        val elements = controllerKey.split("/")

        elements
            .filter { it.isNotEmpty() }
            .forEach { element ->
            controllerName.append(element.replaceFirstChar { it.toUpperCase() })
        }

        controllerName.append("Api")

        return controllerName.toString()
    }
}