package br.com.zup.chave.utils

import br.com.zup.CadastrarChaveRequest
import br.com.zup.TipoChave
import br.com.zup.chave.CadastrarChaveDTO
import br.com.zup.TipoChave.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.TipoConta
import br.com.zup.TipoConta.TIPO_CONTA_DESCONHECIDO

fun CadastrarChaveRequest.toDTO(): CadastrarChaveDTO {
    return CadastrarChaveDTO(
        idCliente = idCliente,
        tipoChave = if (tipoChave != TIPO_CHAVE_DESCONHECIDO) br.com.zup.chave.enums.TipoChave.valueOf(tipoChave.name) else null,
        tipoConta = if (tipoConta != TIPO_CONTA_DESCONHECIDO) br.com.zup.chave.enums.TipoConta.valueOf(tipoConta.name) else null,
        chave = chave
    )
}