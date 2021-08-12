package org.platonos.rest.gen.openapi

class Options(val fileName: String,
              val modelPackageName: String?,
              val apiPackageName: String? = null,
              val generateModels: Boolean = true,
              val generateApiDefintions: Boolean = false,
              val generateApiImplementations: Boolean = false,
              val generator: String? = null,
              val dynamicModels: List<String> = emptyList())