package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.data.dummyImage
import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImageRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.mockito.ArgumentMatchers.anyInt
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ImageHandlerTest : Spek({

    val imageRepo: ImageRepository = mock()
    val imagesModel: ImagesModel = mock()

    describe("getting data") {

        val dummyImage = dummyImage

        val response: Mono<ServerResponse> = ServerResponse.ok().body(BodyInserters.fromObject(10))


        beforeEachGroup {
            runBlocking {
                whenever(imageRepo.getImagesById(anyInt())).thenReturn(dummyImage)
            }
            whenever(imageRepo.save(imagesModel))
                .thenReturn(response)
        }

        it("get data calling the repository") {
            runBlocking {
                val test = imageRepo.getImagesById(2)
                Assertions.assertEquals(test, dummyImage)
            }
        }

        it("testing image save repository"){
            val test = imageRepo.save(imagesModel)
            Assertions.assertNotEquals(test, ServerResponse.ok().body(BodyInserters.fromObject(10)))
        }
    }
})
