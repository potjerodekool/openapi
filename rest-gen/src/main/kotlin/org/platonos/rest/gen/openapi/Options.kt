package org.platonos.rest.gen.openapi

class Options(val fileName: String,
              val modelPackageName: String,
              val apiPackage: String,
              val generateModels: Boolean = true,
              val generateApiDefintions: Boolean = false,
              val generateApiImplementations: Boolean = false,
              val generator: String = "spring")