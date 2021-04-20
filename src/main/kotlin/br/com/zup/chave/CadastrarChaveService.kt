package br.com.zup.chave

import br.com.zup.chave.bcb.BancoCentralClient
import br.com.zup.chave.bcb.CreatePixKeyRequest
import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.itau.ItauClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class CadastrarChaveService(
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val itauClient: ItauClient,
    @Inject private val bancoCentralClient: BancoCentralClient,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun salvar(@Valid chaveDTO: CadastrarChaveDTO): Chave {
        // Verificar se a chave já existe
        if(chaveRepository.existsByChave(chaveDTO.chave)){
            //throw ChaveExistenteException()
        }

        // Buscar dados da conta
        val conta: Conta =
            itauClient.buscarContaPorTipo(chaveDTO.idCliente!!, chaveDTO.tipoConta!!.name).body()?.toModel() ?:
            throw IllegalStateException("cliente não cadastrado no Itaú")

        // Verificar se o CPF informado é o do titular da conta
        if(chaveDTO.tipoChave == TipoChave.CPF && conta.titularCpf != chaveDTO.chave){
            throw IllegalStateException("o cpf difere do cadastro do titular")
        }

        val chave = chaveRepository.save(chaveDTO.toModel(conta)).also {
            logger.info("Chave salva: ${it.id}")
        }

        // Salvar a chave no banco central
        val bcbResponse = bancoCentralClient.cadastrar(CreatePixKeyRequest.from(chave)).also {
            logger.info("Chave enviada para o sistema do BCB com status ${it.status}")
        }

        if(bcbResponse.status != HttpStatus.CREATED){
            throw IllegalStateException("falha ao cadastrar a chave no banco central")
        }

        chave.atualizarChave(bcbResponse.body()!!.key)

        return chave
    }
}