package com.wldrmnd.thumbnail.api.repositories

import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.util.getLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.rangeAsFlow
import org.springframework.data.redis.core.rightPushAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.server.ResponseStatusException
import kotlin.random.Random

@Repository
class ImagesRepositoryRedisImpl(
    private val reactiveRedisTemplate: ReactiveRedisOperations<String, ImagesModel>
): ImagesRepository {

    override suspend fun getImagesById(id: Int): ImagesModel? {
        return reactiveRedisTemplate
            .opsForValue()
            .get(id.toString())
            .awaitFirstOrNull()
    }

    override suspend fun addNewImage(path: String):Int {
        val id = Random.nextInt()

        logger.info("Creating new image with id = $id")

        val image = ImagesModel(id,path)

        val result = reactiveRedisTemplate
            .opsForValue()
            .setAndAwait(id.toString(),image)

        if(!result){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        reactiveRedisTemplate.opsForList().rightPushAndAwait("image",image)
        return id
    }

    override fun getAllImages(): Flow<ImagesModel> {
        return reactiveRedisTemplate
            .opsForList()
            .rangeAsFlow("images",0,-1)
    }

    companion object {

        private val logger = getLogger(ImagesRepositoryRedisImpl::class.java)
    }
}
