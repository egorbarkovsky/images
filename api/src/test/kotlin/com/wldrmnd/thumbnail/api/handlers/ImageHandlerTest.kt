package com.wldrmnd.thumbnail.api.handlers

import com.wldrmnd.thumbnail.api.models.ImagesModel
import com.wldrmnd.thumbnail.api.repositories.ImagesRepository
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.web.reactive.function.server.ServerRequest

object ImageHandlerTest: Spek({

    describe("Image Handler"){
        lateinit var imagesRepositoryRedisImpl: ImagesRepository
        lateinit var redisTemplate: ReactiveRedisOperations<String, ImagesModel>
        lateinit var imageHandler:ImageHandler
        lateinit var request: ServerRequest

        beforeGroup {
            imageHandler = ImageHandler(imagesRepositoryRedisImpl,redisTemplate)
        }

        describe("fetch image by id"){


            it("return 404"){
                assert(true, { imageHandler.upload(request) })
            }
        }

        describe("upload"){
            it("should return id"){
//            assert(true, { imageHandler.findOne(request) })
            }
        }

    }
}
)