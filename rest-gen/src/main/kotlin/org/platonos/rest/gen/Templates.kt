package org.platonos.rest.gen

import org.stringtemplate.v4.ST
import org.stringtemplate.v4.STGroup
import org.stringtemplate.v4.STGroupDir
import java.net.URL
import java.nio.charset.StandardCharsets

object Templates {

    private val group = initStGroup()

    private fun initStGroup(): STGroup {
        val location = javaClass.protectionDomain.codeSource.location

        val root = if (location.toString().endsWith(".jar")) {
            URL("jar:$location!/templates")
        } else {
            URL(location, "templates")
        }

        return STGroupDir(root, StandardCharsets.UTF_8.name(), '<', '>')
    }

    fun getInstanceOf(name: String): ST? {
        return group.getInstanceOf(name)
    }
}