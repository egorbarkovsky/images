package com.wldrmnd.thumbnail.service

import com.wldrmnd.thumbnail.models.ImagesModel
import com.wldrmnd.thumbnail.util.getLogger
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.annotation.PostConstruct
import javax.imageio.ImageIO


@Service
class SubscriberService {

    @Autowired
    private val reactiveRedisTemplate: ReactiveRedisOperations<String, ImagesModel>? = null

    @Value("\${topic.name.image-topic}")
    private val topic: String? = null

    @PostConstruct
    private fun init() {
        reactiveRedisTemplate
            ?.listenTo(ChannelTopic.of(topic!!))
            ?.map {
                it.message
            }
            ?.subscribe {

                resize(it.path,it.name,24,20)
            }

    }


    fun resize(file: String, imageName: String, width: Int, height: Int) {
        logger.info("Process of resizing $imageName into 24x20 thumbnail")

        try {
            val image: BufferedImage  = ImageIO.read(File("$file"))
            val imageScaled: BufferedImage = Scalr.resize(image,
                Scalr.Method.AUTOMATIC,
                Scalr.Mode.AUTOMATIC,
                width, height,
                Scalr.OP_ANTIALIAS)

            ImageIO.write(imageScaled, "png", File(file))
            imageScaled.flush()
        } catch (e: IOException) {
            println(e.message)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            throw e
        }
    }


    companion object {

        var logger = getLogger(SubscriberService::class.java)
    }
}
