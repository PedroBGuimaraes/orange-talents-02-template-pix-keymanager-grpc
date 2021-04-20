package br.com.zup.chave

import br.com.zup.CadastrarChaveRequest
import br.com.zup.CadastrarChaveResponse
import br.com.zup.KeyManagerGrpcServiceGrpc
import br.com.zup.chave.utils.toDTO
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChavePixGRpc(
    @Inject private val cadastrarChaveService: CadastrarChaveService
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastrar(
        request: CadastrarChaveRequest,
        responseObserver: StreamObserver<CadastrarChaveResponse>
    ) {

        val chave = cadastrarChaveService.salvar(request.toDTO())
        responseObserver.onNext(CadastrarChaveResponse.newBuilder().setPixId(chave.id.toString()).build())
        responseObserver.onCompleted()

    }

}