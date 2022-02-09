package org.platonos.rest.generate2

object ClassPathUtils {

    fun useJakartaValidation(): Boolean {
        var enabledValue = System.getProperty("jakarta.validation")
        val isEnabled: Boolean

        if (enabledValue != null) {
            isEnabled = "true" == enabledValue
        } else {
            isEnabled = isClassPresent("jakarta.validation.constraints.NotNull")
        }

        println("useJakartaValidation " + isEnabled)
        return isEnabled
    }

    fun useJakartaServlet(): Boolean {
        var enabledValue = System.getProperty("jakarta.servlet")
        val isEnabled: Boolean

        if (enabledValue != null) {
            isEnabled = "true" == enabledValue
        } else {
            isEnabled = isClassPresent("jakarta.servlet.http.HttpServletRequest")
        }

        println("useJakartaServlet " + isEnabled)
        return isEnabled
    }

    fun isClassPresent(className: String): Boolean {
        try {
            val clazz = javaClass.classLoader.loadClass(className)
            println("Found $className in " + clazz.protectionDomain.codeSource.location)
            return true
        } catch (e: Exception) {
            println("Class is not present " + className)
            return false
        }
    }
}