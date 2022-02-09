package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.junit.jupiter.api.Test
import org.platonos.rest.generate.Build
import org.platonos.rest.generate.ProjectInfo
import org.platonos.rest.generate2.Generator2
import java.io.File

internal class JavaTypeConverterTest {

    private val types = TypesJava()

    private val modelNamingStrategy = DefaultModelNamingStrategy()

    private val javaTypeConverter = JavaTypeConverter(
        types,
        modelNamingStrategy,
        "org.some.model"
    )

    @Test
    fun convert() {

        val options = Options(
            fileName = "api/spec.yaml",
            modelPackageName = "org.some.model",
            apiPackageName = "org.some.api",
            generateModels = true,
            generateApiDefintions = true,
            generateApiImplementations = true
        )

        val build = Build(File("source"))

        val projectInfo = ProjectInfo(
            listOf(),
            listOf(),
            build
        )

        val generator = Generator2(
            options,
            projectInfo
        )

        generator.execute()

        /*
        val parser = OpenApiParser()
        val uri = File("api/spec.yaml")
        val api = OpenApiMerger().merge(uri)
        val getOperation = api.paths["/events"]!!.get
        val schema = getOperation.responses["200"]!!.contentMediaTypes["application/json"]!!.schema

        val type = javaTypeConverter.convert(schema, false)
        println(type)
        */


    }
}