package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImagesRepository
import com.wldrmnd.thumbnail.api.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
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
class ImageHandler(private val imagesRepositoryRedisImpl: ImagesRepository,
                   private val redisTemplate: ReactiveRedisOperations<String, ImagesModel>
) {

    @Value("\${topic.name.image-topic}")
    private val topic: String? = null

    @Value("\${file.path}")
    lateinit var  path: String


    suspend fun findOne(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()

        val imageById = imagesRepositoryRedisImpl.getImagesById(id)
            ?: return ServerResponse.notFound().buildAndAwait()

        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(imageById)
    }

    fun upload(request: ServerRequest): Mono<ServerResponse> {

        return request.body(BodyExtractors.toMultipartData()).flatMap { parts ->
            val map: Map<String, Part> = parts.toSingleValueMap()

            val filePart: FilePart = map["file"]!! as FilePart

            val fileName = filePart.filename()
            val filePath ="/tmp/$fileName"
            filePart.transferTo(File("/tmp/$fileName")).subscribe()
            val id = Random.nextInt()
            val image = ImagesModel(id,fileName,filePath)
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

    companion object {

        private val logger = getLogger(ImageHandler::class.java)
    }
}
