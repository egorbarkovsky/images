package com.wldrmnd.thumbnail.service

import com.wldrmnd.thumbnail.models.ImagesModel
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
                println(it)
//                    getThumbnail(it.path,it.name)
                val file = File(it.path)
                resize(file,24,20)
            }

    }

    fun resize(file: File?, width: Int, height: Int) {
        var image: BufferedImage
        try {
            image = ImageIO.read(file)
            image = Scalr.resize(image,
                Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.FIT_EXACT,
                width,
                height)
            saveToJPG(image, file!!)
            image.flush()
        } catch (e: IOException) {
            println(" resize error")
            println(e.message)
            throw e
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    @Throws(IOException::class)
    private fun saveToJPG(image: BufferedImage, file: File) {
        val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        newImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null)
        ImageIO.write(newImage, "jpg", file)
        newImage.flush()
    }

}