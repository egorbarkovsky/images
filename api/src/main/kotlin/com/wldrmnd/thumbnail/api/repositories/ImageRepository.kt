package com.wldrmnd.thumbnail.api.repositories

import com.wldrmnd.thumbnail.api.models.ImagesModel
import kotlinx.coroutines.flow.Flow
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface ImageRepository {

    suspend fun getImagesById(id: Int): ImagesModel?

    fun save(imagesModel: ImagesModel): Mono<ServerResponse>
}
