package com.wldrmnd.thumbnail.api.configurations

import com.wldrmnd.thumbnail.api.models.ImagesModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import javax.annotation.PreDestroy


@Configuration
class RedisConfig(private val factory: RedisConnectionFactory) {

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, ImagesModel> {
        return ReactiveRedisTemplate(
            factory,
            RedisSerializationContext
                .newSerializationContext<String, ImagesModel>(StringRedisSerializer())
                .value(Jackson2JsonRedisSerializer(ImagesModel::class.java))
                .build()
        )
    }

    @PreDestroy
    fun cleanRedis() {
        factory.connection.flushDb()
    }
}
