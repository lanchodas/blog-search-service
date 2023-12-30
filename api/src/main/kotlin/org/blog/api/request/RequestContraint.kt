package org.blog.api.request

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [StringLengthValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryLength(
    val max: Int,
    val message: String = "검색 키워드 길이를 {max}자 까지만 입력해주세요.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class StringLengthValidator : ConstraintValidator<QueryLength, String> {
    private var maxLength: Int = 0
    override fun initialize(constraintAnnotation: QueryLength) {
        maxLength = constraintAnnotation.max
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.length <= maxLength
    }
}
