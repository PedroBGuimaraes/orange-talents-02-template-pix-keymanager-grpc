package br.com.zup.chave.exceptions

class ChaveExistenteException(
    message: String = "chave já está cadastrada"
) : RuntimeException(message) {
}