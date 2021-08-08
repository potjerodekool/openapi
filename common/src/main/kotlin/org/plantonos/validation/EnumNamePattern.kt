package org.plantonos.validation

import javax.validation.Constraint;
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [EnumNamePatternValidator::class])
annotation class EnumNamePattern(
    val regexp: String,
    val message: String = """must match "{regexp}"""",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<Payload>> = []
)