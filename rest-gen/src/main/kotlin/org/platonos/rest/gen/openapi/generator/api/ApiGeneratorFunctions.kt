package org.platonos.rest.gen.openapi.generator.api

import com.reprezen.kaizen.oasparser.model3.Operation
import org.platonos.rest.gen.doc.JavaDoc
import org.platonos.rest.gen.openapi.api.HttpMethod
import kotlin.text.StringBuilder

fun createJavaDoc(httpMethod: HttpMethod,
                  url: String,
                  operation: Operation,
): JavaDoc {
    val jdb = JavaDocBuilder()

    if (operation.summary != null) {
        jdb.append("* $httpMethod $url : ${operation.summary}").appendNewLine()
    }

    if (operation.description != null) {
        jdb.append("* ${operation.description}").appendNewLine()
    }

    if (operation.hasParameters()) {
        operation.parameters.forEach { parameter ->
            val parameterDescription = parameter.description ?: ""

            jdb.appendNewLine()
            jdb.append("* @param ${parameter.name}")

            if (parameter.isRequired) {
                jdb.append(" (required)")
            }

            if (parameter.description.isNullOrEmpty().not()) {
                jdb.append(" $parameterDescription")
            }
        }

        jdb.appendNewLine()
    }

    if (operation.requestBody?.description != null) {
        val requestDescription = operation.requestBody.description
        jdb.appendNewLine()
        jdb.append("* $requestDescription")
    }

    if (operation.responses.isNotEmpty()) {
        jdb.appendNewLine()
        jdb.append("* @return")

        var responseIndex = 0

        operation.responses.forEach { (statusCode, response) ->
            if (response.description.isNullOrEmpty().not()) {
                jdb.appendNewLine()
                if (responseIndex > 0) {
                    jdb.appendNewLine()
                }
                jdb.append("* ${response.description} (status code $statusCode)")
                responseIndex++
            }
        }
    }

    return JavaDoc(jdb.getJavaDoc())
}

class JavaDocBuilder {

    private val text = StringBuilder()

    fun appendNewLine() {
        if (text.isNotEmpty()) {
            val lastChar = text[text.length -1]

            if (lastChar == '\n') {
                doAppend("*")
            }

            doAppend("\n")
        }
    }

    fun append(text: String): JavaDocBuilder {
        return doAppend(text.replace("\n", "\n*"))
    }

    private fun doAppend(text: String): JavaDocBuilder {
        this.text.append(text)
        return this
    }

    fun getJavaDoc(): String {
        val builder = StringBuilder()
        builder.append("/**\n")
        builder.append(text.toString())
        builder.append("\n*/")
        return builder.toString()
    }
}