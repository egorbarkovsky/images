package com.wldrmnd.thumbnail.service

import com.wldrmnd.thumbnail.models.ImagesModel
import com.wldrmnd.thumbnail.util.getLogger
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.annotation.PostConstruct
import javax.imageio.ImageIO
import kotlin.jvm.Throws


@Service
class SubscriberService {

    @Autowired
    private val reactiveRedisTemplate: ReactiveRedisOperations<String, ImagesModel>? = null

    @Value("\${topic.name.image-topic}")
    private val topic: String? = null

    @Value("\${file.path.new}")
    private val newDir: String? = null

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

    fun resize(file: String, imageName : String,width: Int, height: Int) {
        logger.info(" file path $file")
        // val inputStream: InputStream

        try {
            var image:BufferedImage  = ImageIO.read(File("$file"))
            image = Scalr.
            resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, height, Scalr.OP_ANTIALIAS)
            ImageIO.write(image,"png", File(newDir+imageName))
        } catch (e: IOException) {
            println(e.message)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            throw e
        }
    }

    companion object {

        private val logger = getLogger(SubscriberService::class.java)
    }
}
