package com.wldrmnd.thumbnail.models

import org.springframework.data.annotation.Id

data class ImagesModel (

    @Id
    val id: Int = 0,
    val name: String = "",
    val path: String = ""
)
