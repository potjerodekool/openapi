package org.platonos.rest.openapi.api

class Api(val paths: Map<String, ApiPath>) {

    val requestModels: MutableMap<String, ApiModel> = mutableMapOf()
    val responseModels: MutableMap<String, ApiModel> = mutableMapOf()

}