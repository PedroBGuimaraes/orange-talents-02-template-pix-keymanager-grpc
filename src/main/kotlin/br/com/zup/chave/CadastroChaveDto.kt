package br.com.zup.chave

import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.enums.TipoConta
import br.com.zup.chave.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ChavePix
@Introspected
data class CadastrarChaveDTO(
    @field:ValidUUID @field:NotBlank
    val idCliente: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:NotNull
    val tipoConta: TipoConta?,
    @field:Size(max = 77)
    val chave: String?
) {

    fun toModel(conta: Conta): Chave = Chave(
        chave = if(tipoChave == TipoChave.ALEATORIA) UUID.randomUUID().toString() else chave!!,
        idCliente = UUID.fromString(idCliente),
        tipoChave = tipoChave!!,
        tipoConta = tipoConta!!,
        conta = conta
    )
}