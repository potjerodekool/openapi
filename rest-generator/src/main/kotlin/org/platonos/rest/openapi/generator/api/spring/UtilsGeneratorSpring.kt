package org.platonos.rest.openapi.generator.api.spring

import org.platonos.rest.generate2.ClassPathUtils
import org.platonos.rest.openapi.generator.api.UtilsGenerator

class UtilsGeneratorSpring : UtilsGenerator {

    override fun generateUtils(packageName: String): String {
        val jakarta = ClassPathUtils.useJakartaServlet()

        val result = String(javaClass.classLoader.getResourceAsStream("utils/ApiUtils.java").readAllBytes())
            .replace("<packageName>", packageName)

        if (jakarta) {
            return result.replace("javax.servlet.http.HttpServletRequest", "jakarta.servlet.http.HttpServletRequest")
        }

        return result;
    }
}