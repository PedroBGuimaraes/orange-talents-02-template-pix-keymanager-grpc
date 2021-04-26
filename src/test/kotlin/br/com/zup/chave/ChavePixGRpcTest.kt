package br.com.zup.chave

import br.com.zup.*
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ChavePixGRpcTest (val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
                                 @Inject private val chaveRepository: ChaveRepository
){

    @Test
    fun `DEVE cadastrar chave Pix`(){
        val response = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        assertNotNull(response.pixId)
    }

    @Test
    fun `DEVE cadastrar chave Pix Aleatoria`() {
        val response = grpcClient.cadastrar(
            CadastrarChaveRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.ALEATORIA)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        assertNotNull(response.pixId)

        val response1 = grpcClient.cadastrar(
            CadastrarChaveRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.ALEATORIA)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        assertNotNull(response1.pixId)

        val response2 = grpcClient.cadastrar(
            CadastrarChaveRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.ALEATORIA)
                .setChave("02467781054")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        assertNotNull(response2.pixId)
    }

    @Test
    fun `NAO DEVE cadastrar chave Pix igual`(){
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

    @Test
    fun `DEVE buscar chave Pix`(){
        val responseChave = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.CPF)
            .setChave("06628726061")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        val responseBuscar = grpcClient.buscar(BuscarChaveRequest.newBuilder()
            .setPixId(BuscarChaveRequest.BuscaPorPixId.newBuilder()
                .setIdChave(responseChave.pixId)
                .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
                .build()
            )
            .setChave("06628726061")
            .build()
        )

        assertEquals(responseBuscar.idPix, responseChave.pixId)
    }

    @Test
    fun `DEVE listar chaves Pix cliente`(){
        val response = grpcClient.listar(ListarChaveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()
        )

        assertEquals(response.chavesCount, 4)
    }

    @Test
    fun `DEVE deletar chave Pix`(){
        chaveRepository.deleteAll()
        val responseChave = grpcClient.cadastrar(CadastrarChaveRequest.newBuilder()
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CPF)
            .setChave("86135457004")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        val responseRemover = grpcClient.remover(
            RemoverChaveRequest.newBuilder()
                .setIdChave(responseChave.pixId)
                .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
                .build()
        )

        assertEquals(0 ,chaveRepository.findAll().size)
    }



}

@Factory
class Clients {
    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub{
        return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
    }
}