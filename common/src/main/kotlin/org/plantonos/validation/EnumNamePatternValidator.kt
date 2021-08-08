package org.plantonos.validation

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumNamePatternValidator : ConstraintValidator<EnumNamePattern, Enum<*>> {

    private lateinit var pattern: Pattern

    override fun initialize(annotation: EnumNamePattern) {
        pattern = try {
            Pattern.compile(annotation.regexp)
        } catch (e: PatternSyntaxException) {
            throw IllegalArgumentException("Invalid regex ${annotation.regexp}", e)
        }
    }

    override fun isValid(value: Enum<*>?, context: ConstraintValidatorContext?): Boolean {
        return value == null || pattern.matcher(value.name).matches()
    }
}