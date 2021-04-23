package br.com.zup.chave.utils

import br.com.zup.BuscarChaveRequest
import br.com.zup.BuscarChaveRequest.BuscaCase.CHAVE
import br.com.zup.BuscarChaveRequest.BuscaCase.PIXID
import br.com.zup.CadastrarChaveRequest
import br.com.zup.TipoChave.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.TipoConta.TIPO_CONTA_DESCONHECIDO
import br.com.zup.chave.CadastrarChaveDTO
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun CadastrarChaveRequest.toDTO(): CadastrarChaveDTO {
    return CadastrarChaveDTO(
        idCliente = idCliente,
        tipoChave = if (tipoChave != TIPO_CHAVE_DESCONHECIDO) br.com.zup.chave.enums.TipoChave.valueOf(tipoChave.name) else null,
        tipoConta = if (tipoConta != TIPO_CONTA_DESCONHECIDO) br.com.zup.chave.enums.TipoConta.valueOf(tipoConta.name) else null,
        chave = chave
    )
}

fun BuscarChaveRequest.toFiltro(validator: Validator): Filtro {
    val filtro = when(buscaCase) {
        PIXID -> pixId.let { Filtro.BuscaPorPixId(it.idCliente, it.idChave) }
        CHAVE -> Filtro.BuscaPorChave(chave)
        else -> Filtro.Invalido
    }

    val erros = validator.validate(filtro)
    if(erros.isNotEmpty()) throw ConstraintViolationException(erros)

    return filtro
}