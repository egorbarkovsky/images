package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.models.ImageAvailableView
import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImagesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.io.File


@Component
class ImageHandler(private val imagesRepositoryRedisImpl: ImagesRepository,
                   private val webClient: WebClient) {

    suspend fun addNewImage(request: ServerRequest): ServerResponse = coroutineScope {
        val (_, path) = request.awaitBody<ImagesModel>()
        val resp: Int = imagesRepositoryRedisImpl.addNewImage(path)
        ServerResponse.ok().bodyValueAndAwait(resp)
    }

    suspend fun findOne(request: ServerRequest) : ServerResponse {
        val id = request.pathVariable("id").toInt()

        val imageById = imagesRepositoryRedisImpl.getImagesById(id)
                ?: return ServerResponse.notFound().buildAndAwait()

        return ServerResponse
                .ok()
                .json()
                .bodyValueAndAwait(imageById)
    }

    suspend fun findAll(request: ServerRequest) : ServerResponse =
            ServerResponse.ok().json().bodyAndAwait(imagesRepositoryRedisImpl.getAllImages())

    suspend fun findOneInMem(request: ServerRequest) : ServerResponse = coroutineScope {
        val id = request.pathVariable("id").toInt()

        val image = async {
            imagesRepositoryRedisImpl.getImagesById(id)
        }

        val quantity = async {
            webClient
                    .get()
                    .uri("/v1/image/$id/available")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<Int>()
        }

        ServerResponse
                .ok()
                .json()
                .bodyValueAndAwait(ImageAvailableView(image.await()!!, quantity.await()))
    }

   fun upload(request: ServerRequest) : Mono<ServerResponse> {

       return request.body(BodyExtractors.toMultipartData()).flatMap {
           parts ->
           val map: Map<String, Part> = parts.toSingleValueMap()
           val filePart: FilePart = map["file"]!! as FilePart
           val fileName = filePart.filename()
           filePart.transferTo(File("tmp/$fileName",""))
           ServerResponse.ok().body(BodyInserters.fromObject("ok"))
       }
    }
}
