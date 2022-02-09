package org.platonos.rest.model

import org.junit.jupiter.api.Test
import org.platonos.rest.openapi.*
import org.platonos.rest.generate2.ApiBuilder
import java.io.File

internal class ModelTest {

    @Test
    fun test() {
        val openApi = OpenApiMerger().merge(File("C:\\projects\\rest-dto\\demo\\openapi\\spec.yml"))

        val types = TypesJava()
        val namingStrategy = DefaultModelNamingStrategy()

        val typeConverter = JavaTypeConverter(
            types,
            namingStrategy,
            "org.some.model"
        )

        val config = OpenApiGeneratorConfigurationBuilder()
            .build()

        val builder = ApiBuilder(config, typeConverter, namingStrategy)
        val api = builder.buildModels(openApi)
        println(api)

    }
}