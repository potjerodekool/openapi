package org.platonos.rest.gen.openapi.api

class ApiPath(val path: String,
              val post: ApiOperation? = null,
              val get: ApiOperation? = null,
              val put: ApiOperation? = null,
              val patch: ApiOperation? = null,
              val delete: ApiOperation? = null)