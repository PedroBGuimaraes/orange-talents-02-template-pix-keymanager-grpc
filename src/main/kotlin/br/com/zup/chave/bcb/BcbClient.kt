package br.com.zup.chave.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType.APPLICATION_XML
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}")
@Suppress("unused")
interface BancoCentralClient {
    @Post(
        value = "/api/v1/pix/keys",
        produces = [APPLICATION_XML],
        consumes = [APPLICATION_XML]
    )
    fun cadastrar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(
        value = "/api/v1/pix/keys/{key}",
        produces = [APPLICATION_XML]
    )
    fun remover(@Body request: DeletePixKeyRequest, @PathVariable key: String): HttpResponse<Unit>

    @Get(
        value = "/api/v1/pix/keys/{key}",
        consumes = [APPLICATION_XML]
    )
    fun buscarPorChave(@PathVariable key: String): HttpResponse<DetailsPixKeyDto>

}