package br.com.zup.chave.utils

import br.com.zup.BuscarChaveResponse
import br.com.zup.TipoChave.valueOf
import br.com.zup.TipoConta
import br.com.zup.chave.ChaveDetails
import com.google.protobuf.Timestamp
import java.time.ZoneId

class BuscarChaveResponseConverter {
    fun converter(chaveInfo: ChaveDetails): BuscarChaveResponse {
        return BuscarChaveResponse.newBuilder()
            .setIdCliente(chaveInfo.idCliente?.toString() ?: "")
            .setIdPix(chaveInfo.idPix?.toString() ?: "")
            .setChave(BuscarChaveResponse.ChaveInfo.newBuilder()
                .setTipo(valueOf(chaveInfo.tipo.name))
                .setChave(chaveInfo.chave)
                .setConta(BuscarChaveResponse.ChaveInfo.ContaInfo.newBuilder()
                    .setTipo(TipoConta.valueOf(chaveInfo.tipoConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setTitularNome(chaveInfo.conta.titularNome)
                    .setTitularCpf(chaveInfo.conta.titularCpf)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setConta(chaveInfo.conta.numero)
                    .build()
                )
                .setCriadaEm(chaveInfo.criadaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
            )
            .build()
    }
}