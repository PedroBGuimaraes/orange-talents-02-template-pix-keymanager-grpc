package br.com.zup.chave

import br.com.zup.CadastrarChaveRequest
import br.com.zup.KeyManagerGrpcServiceGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.exceptions.ChaveExistenteException
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.AssertTrue

@MicronautTest(transactional = false)
internal class ChavePixGRpcTest (val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
                                 @Inject private val chaveRepository: ChaveRepository
){

    @Test
    fun `deve cadastrar chave Pix`(){
        chaveRepository.deleteAll()
        val response = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.ALEATORIA)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        assertNotNull(response.pixId)
    }

    @Test
    fun `nao deve cadastrar chave Pix igual`(){
        chaveRepository.deleteAll()

        val response = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        val error = assertThrows<RuntimeException> {
            val response = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.CPF)
                .setChave("02467781054")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
            )
        }

        with(error){
            assertEquals("UNKNOWN", error.localizedMessage)
        }
    }


}

@Factory
class Clients {
    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub{
        return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
    }
}