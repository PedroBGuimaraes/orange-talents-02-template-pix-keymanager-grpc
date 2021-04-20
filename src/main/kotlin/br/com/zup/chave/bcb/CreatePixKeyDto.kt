package br.com.zup.chave.bcb

import br.com.zup.chave.Chave
import br.com.zup.chave.Conta
import br.com.zup.chave.enums.TipoChave
import br.com.zup.chave.enums.TipoConta
import java.time.LocalDateTime

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
){
    companion object {
        fun from(chave: Chave): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = KeyType.by(chave.tipoChave),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = Conta.ITAU_UNIBANCO_ISPB,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numero,
                    accountType = BankAccount.AccountType.by(chave.tipoConta)
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = chave.conta.titularNome,
                    taxIdNumber = chave.conta.titularCpf
                )
            )
        }
    }
}

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
)

enum class KeyType(val tipoChave: TipoChave?) {
    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIA);

    companion object {
        private val map = values().associateBy(KeyType::tipoChave)

        fun by(tipoChave: TipoChave): KeyType {
            return map[tipoChave] ?: throw IllegalArgumentException("KeyType não encontrado para $tipoChave")
        }
    }
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {
    enum class AccountType() {
        CACC,
        SVGS;

        companion object {
            fun by(tipo: TipoConta): AccountType {
                return when(tipo) {
                    TipoConta.CONTA_CORRENTE -> CACC
                    TipoConta.CONTA_POUPANCA -> SVGS
                }
            }
        }
    }
}

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String,
) {
    enum class OwnerType {
        NATURAL_PERSON,
        LEGAL_PERSON,
    }
}