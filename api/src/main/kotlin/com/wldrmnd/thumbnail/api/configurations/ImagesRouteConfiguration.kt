package com.wldrmnd.thumbnail.api.configurations

import com.wldrmnd.thumbnail.api.handlers.ImageHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
import org.springframework.http.MediaType

@Configuration
class ImagesRouteConfiguration {

    @Bean
    fun imageRoute(imagesHandler: ImageHandler) = coRouter {

        "/images/v1".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", imagesHandler::findAll)
                GET("/{id}", imagesHandler::findOne)
                GET("/{id}/available", imagesHandler::findOneInMem)
                POST("", imagesHandler::addNewImage)
//                POST("/upload", imagesHandler::upload)
            }
        }
    }

    @Bean
    fun route(imagesHandler: ImageHandler): RouterFunction<ServerResponse> {

        return RouterFunctions
                .route(RequestPredicates.POST("/upload").and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
                    {
                        imagesHandler.upload(it)
                    })
    }

}
