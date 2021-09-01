package org.platonos.rest.generate.openapi;

import com.reprezen.kaizen.oasparser.model3.Schema

interface UnitSchemaVisitor<P> {

    fun visitSchema(schema: Schema, param: P)

    fun visitProperty(propertyName: String, schema: Schema, param: P)
}
