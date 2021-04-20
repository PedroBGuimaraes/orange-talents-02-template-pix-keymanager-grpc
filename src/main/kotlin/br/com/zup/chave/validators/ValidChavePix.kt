package br.com.zup.chave

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePix(
    val message: String = "chave PIX inválida para o tipo informado",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = [],
)

@Singleton
class ChavePixValidator: ConstraintValidator<ChavePix, CadastrarChaveDTO> {
    override fun isValid(
        value: CadastrarChaveDTO?,
        annotationMetadata: AnnotationValue<ChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoChave == null)
            return false

        return value.tipoChave.valida(value.chave)
    }
}
