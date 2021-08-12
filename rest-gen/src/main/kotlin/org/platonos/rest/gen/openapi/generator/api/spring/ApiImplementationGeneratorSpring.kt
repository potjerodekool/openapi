package org.platonos.rest.gen.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.element.builder.Builders.annotation
import org.platonos.rest.gen.element.builder.Builders.method
import org.platonos.rest.gen.element.builder.Builders.methodInvocation
import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.IdentifierExpression
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.generator.api.ApiImplementationGenerator
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.statement.Statement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.util.Functions.replaceFirstChar

class ApiImplementationGeneratorSpring : ApiImplementationGenerator, AbstractGenerator() {

    private val apiBuilder = Builders.typeElement()

    override fun init(
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        url: String,
        packageElement: PackageElement
    ) {
        super.init(config, platformSupport, url, packageElement)


        val controllerName = createControllerName(url)

        apiBuilder.withAnnotation(
            annotation()
                .withType(DeclaredType("io.swagger.annotations.Api"))
                .build()
        )
        apiBuilder.withAnnotation(
            annotation()
                .withType(DeclaredType("org.springframework.web.bind.annotation.RestController"))
                .build()
        )

        val qualifiedApiName = "${packageElement.getQualifiedName()}.${createApiName(url)}"

        apiBuilder.withKind(ElementKind.CLASS)
        apiBuilder.withSimpleName(controllerName)
        apiBuilder.withInterface(DeclaredType(qualifiedApiName))
        apiBuilder.withEnclosingElement(packageElement)
    }

    private fun createControllerName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Controller"
    }

    override fun generateApiImplementation(url: String, path: Path) {
        if (path.post != null) {
            generatePostMethod(url, path.post)
        }

        /*
        if (path.get != null) {
            generateGetMethod(url, path.get)
        }

        if (path.put != null) {
            generatePutMethod(url, path.put)
        }

        if (path.patch != null) {
            generatePatchMethod(url, path.patch)
        }

        if (path.delete != null) {
            generateDeleteMethod(url, path.delete)
        }

         */
    }


    private fun generatePostMethod(url: String, post: Operation) {
        val method = method()
            .withAnnotation(createOverrideAnnotation())
            .withModifier(Modifier.PUBLIC)
            .withReturnType(
                DeclaredType(
                    qualifiedName = "org.springframework.http.ResponseEntity",
                    listOf(DeclaredType("java.lang.Void"))
                )
            )
            .withSimpleName("create")

        val bodyParameter = createBodyParameter(post)
            .withModifier(Modifier.FINAL)
            .build()
        method.withParameter(bodyParameter)
        method.withParameter(createHttpServletRequestParameter()
            .withModifier(Modifier.FINAL)
            .build())

        val statements = ArrayList<Statement>()

        val createLocationCall =  methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("${apiPackage}.ApiUtils"),
                    IdentifierExpression("createLocation"),
                )
            )
            .withParameter(
                IdentifierExpression("httpServletRequest"),
            )
            .withParameter(
                IdentifierExpression("null")
            )
            .build()

        val mi = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("org.springframework.http.ResponseEntity"),
                    IdentifierExpression("created")
                )
            )
            .withParameter(createLocationCall)
            .build()

        val invokeBuild = methodInvocation()
            .withSelect(FieldAccess(mi, IdentifierExpression("build")))

        statements += ReturnStatement(invokeBuild.build())

        method.withBody(BlockStatement(statements))
        apiBuilder.withEnclosedElement(method.build())
    }

    private fun generateGetMethod(url: String, get: Operation) {
        TODO("Not yet implemented")
    }

    private fun generatePutMethod(url: String, put: Operation) {
        TODO("Not yet implemented")
    }

    private fun generatePatchMethod(url: String, patch: Operation) {
        TODO("Not yet implemented")
    }

    private fun generateDeleteMethod(url: String, delete: Operation) {
        TODO("Not yet implemented")
    }

    override fun getApiImplementation(): TypeElement {
        return apiBuilder.build()
    }

    private fun createOverrideAnnotation(): Annotation {
        return annotation()
            .withType(DeclaredType("java.lang.Override"))
            .build()
    }
}