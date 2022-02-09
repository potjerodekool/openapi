package org.platonos.rest.generate2.api

import org.platonos.rest.openapi.api.ApiPath

interface ApiGenerator {

    fun init()

    fun generate(url: String, path: ApiPath)

    fun finish();
}