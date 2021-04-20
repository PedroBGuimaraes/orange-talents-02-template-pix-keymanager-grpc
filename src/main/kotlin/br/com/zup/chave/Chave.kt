package br.com.zup.chave

import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.enums.TipoConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid

@Entity
class Chave(
    @Column(nullable = false, unique = true)
    var chave: String,
    @Column(nullable = false)
    val idCliente: UUID,
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val tipoConta: TipoConta,
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val tipoChave: TipoChave,

    @field:Valid
    @Embedded
    val conta: Conta
) {
    @Id @GeneratedValue
    var id: UUID? = null
        private set

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    fun atualizarChave(chave: String) {
        if(tipoChave == TipoChave.ALEATORIA){
            this.chave = chave
        }
    }
}