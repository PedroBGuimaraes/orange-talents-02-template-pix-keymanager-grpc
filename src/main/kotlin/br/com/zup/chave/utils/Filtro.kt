package br.com.zup.chave.utils

import br.com.zup.chave.ChaveDetails
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.bcb.BancoCentralClient
import br.com.zup.chave.exceptions.ObjetoNaoEncontradoException
import br.com.zup.chave.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

// classe sealed -> aberta a herança para as classes internas, "enum com esteroides".
@Introspected
sealed class Filtro {
    abstract fun filtrar(repository: ChaveRepository, bancoCentralClient: BancoCentralClient): ChaveDetails

    @Introspected
    data class BuscaPorPixId(
        @field:NotBlank @field:ValidUUID val clienteId: String,
        @field:NotBlank @field:ValidUUID val pixId: String
    ) : Filtro() {
        override fun filtrar(repository: ChaveRepository, bancoCentralClient: BancoCentralClient): ChaveDetails {
            val uuidChave = UUID.fromString(pixId)
            val uuidCliente = UUID.fromString(clienteId)

            return repository
                .findByIdAndIdCliente(uuidChave, uuidCliente)
                .map(ChaveDetails::of)
                .orElseThrow { ObjetoNaoEncontradoException("chave não existe ou não pertence ao cliente") }
        }
    }

    @Introspected
    data class BuscaPorChave(
        @field:NotBlank @field:Size(max = 77) val chave: String
    ) : Filtro() {
        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun filtrar(repository: ChaveRepository, bancoCentralClient: BancoCentralClient): ChaveDetails {
            return repository
                .findByChave(chave)
                .map(ChaveDetails::of)
                .orElseGet{
                    logger.info("consultando chave ($chave) no banco central")

                    val response = bancoCentralClient.buscarPorChave(chave)
                    if(response.status != HttpStatus.OK) {
                        throw ObjetoNaoEncontradoException("chave não encontrada")
                    }

                    response.body()?.toModel()
                }
        }
    }

    object Invalido : Filtro() {
        override fun filtrar(repository: ChaveRepository, bancoCentralClient: BancoCentralClient): ChaveDetails {
            throw IllegalArgumentException("chave pix inválida")
        }
    }
}
