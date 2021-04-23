package br.com.zup.chave.bcb

import br.com.zup.chave.Conta

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.ITAU_UNIBANCO_ISPB,
)