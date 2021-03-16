package com.wldrmnd.thumbnail.api.repositories

import com.wldrmnd.thumbnail.api.models.ImagesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.server.ResponseStatusException
import kotlin.random.Random

@Repository
class ImagesRepositoryRedisImpl(private val reactiveRedisTemplate: ReactiveRedisOperations<String, ImagesModel>): ImagesRepository{

    override suspend fun getImagesById(id: Int): ImagesModel? {
        return reactiveRedisTemplate
            .opsForValue()
            .get(id.toString())
            .awaitFirstOrNull()
    }

    override suspend fun addNewImage(path: String):Int {
        val id = Random.nextInt()

        println(" creating new image with id = $id")

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
}