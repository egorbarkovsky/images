package com.wldrmnd.thumbnail.api.repositories

import com.wldrmnd.thumbnail.api.models.ImagesModel
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {

    suspend fun getImagesById(id: Int): ImagesModel?

    suspend fun addNewImage(path: String): Int

    fun getAllImages(): Flow<ImagesModel>
}
