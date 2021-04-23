package br.com.zup.chave

import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.enums.TipoConta
import java.time.LocalDateTime
import java.util.*

data class ChaveDetails(
    val idPix: UUID? = null,
    val idCliente: UUID? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: Conta,
    val criadaEm: LocalDateTime,
) {
    companion object {
        fun of(chave: Chave): ChaveDetails {
            return ChaveDetails(
                idPix = chave.id,
                idCliente = chave.idCliente,
                tipo = chave.tipoChave,
                chave = chave.chave,
                tipoConta = chave.tipoConta,
                conta = chave.conta,
                criadaEm = chave.criadaEm,
            )
        }
    }
}