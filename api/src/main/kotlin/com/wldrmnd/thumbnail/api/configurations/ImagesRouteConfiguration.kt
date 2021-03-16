package com.wldrmnd.thumbnail.api.configurations

import com.wldrmnd.thumbnail.api.handlers.ImageHandler
import com.wldrmnd.thumbnail.api.util.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*

@Configuration
class ImagesRouteConfiguration {

    @Bean
    fun imageRoute(imagesHandler: ImageHandler) = coRouter {
        logger.info("GET /images/v1/{id}")

        "/images/v1".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/{id}", imagesHandler::findOne)
            }
        }
    }

    @Bean
    fun route(imagesHandler: ImageHandler): RouterFunction<ServerResponse> {
        logger.info("POST /upload")

        return RouterFunctions
            .route(RequestPredicates.POST("/upload")
                .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
                {
                    imagesHandler.upload(it)
                })
    }

    companion object {

        private val logger = getLogger(ImagesRouteConfiguration::class.java)
    }
}

