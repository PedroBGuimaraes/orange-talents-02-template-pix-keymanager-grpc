package br.com.zup.chave

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class Conta(
    @field:NotBlank
    @Column(name = "conta_instituicao", nullable = false)
    val instituicao: String,
    @field:NotBlank
    @Column(name = "conta_titular_nome", nullable = false)
    val titularNome: String,
    @field:NotBlank @field:Size(max = 11, min = 11)
    @Column(name = "conta_titular_cpf", nullable = false)
    val titularCpf: String,
    @field:NotBlank @field:Size(max = 6)
    @Column(name = "conta_agencia", nullable = false)
    val agencia: String,
    @field:NotBlank @field:Size(max = 6)
    @Column(name = "conta_numero", nullable = false)
    val numero: String,
) {
    companion object {
        val ITAU_UNIBANCO_ISPB: String = "60701190"
    }
}
