package com.wldrmnd.thumbnail.api.repositories

import com.wldrmnd.thumbnail.api.models.ImagesModel
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Repository
class RedisImageRepository(private val reactiveRedisTemplate: ReactiveRedisOperations<String, ImagesModel>)
    : ImageRepository {

    @Value("\${topic.name.image-topic}")
    val topic: String? = null


    override suspend fun getImagesById(id: Int): ImagesModel? {
        return reactiveRedisTemplate
            .opsForValue()
            .get(id.toString())
            .awaitFirstOrNull()
    }

    override fun save(imagesModel: ImagesModel): Mono<ServerResponse> {
        reactiveRedisTemplate.convertAndSend(topic!!, imagesModel).subscribe()

        val data = reactiveRedisTemplate.opsForValue().set(imagesModel.id.toString(), imagesModel)

        return data.flatMap {
            val g = it.and(true)
            if (!g) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            }
            reactiveRedisTemplate.opsForList().rightPush("image", imagesModel)
            ServerResponse.ok().body(BodyInserters.fromObject(imagesModel.id))
        }
    }
}
