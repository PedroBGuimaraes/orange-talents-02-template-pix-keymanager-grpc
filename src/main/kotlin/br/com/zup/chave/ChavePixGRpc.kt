package br.com.zup.chave

import br.com.zup.*
import br.com.zup.chave.bcb.BancoCentralClient
import br.com.zup.chave.bcb.CreatePixKeyRequest
import br.com.zup.chave.bcb.DeletePixKeyRequest
import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.exceptions.ChaveExistenteException
import br.com.zup.chave.itau.ItauClient
import br.com.zup.chave.utils.BuscarChaveResponseConverter
import br.com.zup.chave.utils.toDTO
import br.com.zup.chave.utils.toFiltro
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
class ChavePixGRpc(
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val bancoCentralClient: BancoCentralClient,
    @Inject private val itauClient: ItauClient,
    @Inject private val validator: Validator
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastrar(
        request: CadastrarChaveRequest,
        responseObserver: StreamObserver<CadastrarChaveResponse>
    ) {
        val chaveDTO = request.toDTO()

        // Verificar se a chave já existe, caso a chave não seja Null or Blank
        if(!!chaveDTO.chave.isNullOrBlank()&&chaveRepository.existsByChave(chaveDTO.chave)){
            throw ChaveExistenteException()
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

        responseObserver.onNext(CadastrarChaveResponse.newBuilder().setPixId(chave.id.toString()).build())
        responseObserver.onCompleted()

    }

    override fun remover(
        request: RemoverChaveRequest,
        responseObserver: StreamObserver<Empty>
    ) {

        val uuidChave = UUID.fromString(request.idChave)
        val uuidCliente = UUID.fromString(request.idCliente)

        val chave = chaveRepository.findByIdAndIdCliente(uuidChave, uuidCliente)
            .orElseThrow { throw Exception("chave não existe ou não pertence ao cliente") }

        bancoCentralClient.remover(DeletePixKeyRequest(chave.chave), chave.chave)
            .let {
                if (it.status != HttpStatus.OK) {
                    println("Erro: "+it.status)
                    throw IllegalStateException("erro ao remover a chave do banco central")
                }
            }

        chaveRepository.deleteById(uuidChave)
        logger.info("Chave deletada do sistema do BCB com status OK")
        responseObserver.onNext(Empty.newBuilder().build())
        responseObserver.onCompleted()
    }

    override fun buscar(
        request: BuscarChaveRequest,
        responseObserver: StreamObserver<BuscarChaveResponse>
    ) {

        val filtro = request.toFiltro(validator)
        val resultado = filtro.filtrar(chaveRepository, bancoCentralClient)

        responseObserver.onNext(BuscarChaveResponseConverter().converter(resultado))
        responseObserver.onCompleted()
    }


}