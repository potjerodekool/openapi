package org.platonos.rest.gen.openapi;

import com.reprezen.kaizen.oasparser.model3.Schema

interface SchemaVisitor<P,R> {

    fun visitSchema(schema: Schema, param: P): R

    fun visitProperty(propertyName: String, schema: Schema, param: P): R
}
