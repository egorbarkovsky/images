package com.wldrmnd.thumbnail.api.models

import org.springframework.data.annotation.Id

data class ImagesModel (
        @Id
        val id : Int = 0,
        val path : String = ""
)