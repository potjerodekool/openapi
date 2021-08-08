package org.platonos.rest.gen

class Config {

    var packageName: String = ""
    var excludes: List<String> = emptyList()
    var mappings: MutableMap<String, Map<String, String>> = mutableMapOf()
}