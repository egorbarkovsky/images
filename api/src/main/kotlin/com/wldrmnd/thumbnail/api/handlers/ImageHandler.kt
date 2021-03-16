package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.models.ImageAvailableView
import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImagesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.random.Random


@Component
class ImageHandler(private val imagesRepositoryRedisImpl: ImagesRepository
                   , private val webClient: WebClient,
                   private val redisTemplate: ReactiveRedisOperations<String, ImagesModel>
) {

    @Value("\${topic.name.image-topic}")
    private val topic: String? = null

    @Value("\${file.path}")
    private val path: String? = null

    suspend fun addNewImage(request: ServerRequest): ServerResponse = coroutineScope {
        //val (_, path) = request.awaitBody<ImagesModel>()
//        val path = async {
//            webClient
//                    .post()
//                    .uri("/upload")
////                    .body(BodyInserters.FormInserter)
//                    .retrieve()
//                    .awaitBody<String>()
//        }.await()
        val path = "test"
        val resp: Int = imagesRepositoryRedisImpl.addNewImage(path)
        ServerResponse.ok().bodyValueAndAwait(resp)
    }

    suspend fun findOne(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()

        val imageById = imagesRepositoryRedisImpl.getImagesById(id)
            ?: return ServerResponse.notFound().buildAndAwait()

        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(imageById)
    }

    suspend fun findAll(request: ServerRequest): ServerResponse =
        ServerResponse.ok().json().bodyAndAwait(imagesRepositoryRedisImpl.getAllImages())

    suspend fun findOneInMem(request: ServerRequest): ServerResponse = coroutineScope {
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

    fun upload(request: ServerRequest): Mono<ServerResponse> {

        return request.body(BodyExtractors.toMultipartData()).flatMap { parts ->
            val map: Map<String, Part> = parts.toSingleValueMap()
            val filePart: FilePart = map["file"]!! as FilePart
            saveFile(filePart)
            val fileName = filePart.filename()
            val id = Random.nextInt()
            val image = ImagesModel(id, "$path$fileName")
            redisTemplate.convertAndSend(topic!!, image).subscribe()
            val data = redisTemplate.opsForValue().set(id.toString(), image)
            data.flatMap { it ->
                val g = it.and(true)
                if (!g) {
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
                }
                println(it)
                redisTemplate.opsForList().rightPush("image", image)
                ServerResponse.ok().body(BodyInserters.fromObject(id))
            }
        }
    }


    private fun saveFile(filePart: FilePart): Mono<File>? {
        val target: Path = Paths.get(path).resolve(filePart.filename())
        return try {
            Files.deleteIfExists(target)
            val file: File = Files.createFile(target).toFile()
            filePart.transferTo(file)
                .map { r: Void? -> file }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
