package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImageRepository
import com.wldrmnd.thumbnail.api.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.io.File
import kotlin.random.Random


@Component
class ImageHandler(private val imageRepositoryRedisImpl: ImageRepository) {

    @Value("\${topic.name.image-topic}")
    private val topic: String? = null

    @Value("\${file.path}")
    lateinit var path: String

    suspend fun findOne(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        logger.info("Query id is $id")

        val imageById = imageRepositoryRedisImpl.getImagesById(id)
            ?: return ServerResponse.notFound().buildAndAwait()
        logger.info("Image details:  $imageById")
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
            imageRepositoryRedisImpl.save(image)
        }
    }


    companion object {

        private val logger = getLogger(ImageHandler::class.java)
    }
}
