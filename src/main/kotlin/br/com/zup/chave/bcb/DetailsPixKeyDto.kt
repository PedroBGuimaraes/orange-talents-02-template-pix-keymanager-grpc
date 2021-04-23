package br.com.zup.chave.bcb

import br.com.zup.chave.ChaveDetails
import br.com.zup.chave.Conta
import br.com.zup.chave.enums.TipoConta
import br.com.zup.chave.utils.Instituicoes
import java.time.LocalDateTime

data class DetailsPixKeyDto(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
) {
    fun toModel(): ChaveDetails{
        return ChaveDetails(
            tipo = keyType.tipoChave!!,
            chave = key,
            tipoConta = when(this.bankAccount.accountType){
                BankAccount.AccountType.CACC -> TipoConta.CONTA_CORRENTE
                BankAccount.AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = Conta(
                titularNome = owner.name,
                titularCpf = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                instituicao = Instituicoes.nome(bankAccount.participant)
            ),
            criadaEm = createdAt,
        )
    }
}