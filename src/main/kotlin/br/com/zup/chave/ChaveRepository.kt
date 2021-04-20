package br.com.zup.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository: JpaRepository<Chave, UUID>{
    fun existsByChave(chave: String?): Boolean
    fun findByIdAndIdCliente(id: UUID, idCliente: UUID): Optional<Chave>
    fun findByChave(chave: String): Optional<Chave>
    fun findByIdCliente(idCliente: UUID): List<Chave>
}