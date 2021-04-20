package br.com.zup.chave.itau

import br.com.zup.chave.Conta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.url}")
interface ItauClient {
    @Get("/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscarContaPorTipo(@PathVariable idCliente: String, @QueryValue tipo: String): HttpResponse<ContaResponse>
}

data class ContaResponse (
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
){

    fun toModel(): Conta {
        return Conta(
            instituicao = instituicao.nome,
            titularCpf = titular.cpf,
            titularNome = titular.nome,
            agencia = agencia,
            numero = numero
        )
    }
}

data class TitularResponse(val nome: String, val cpf: String)
data class InstituicaoResponse(val nome: String, val ispb: String)