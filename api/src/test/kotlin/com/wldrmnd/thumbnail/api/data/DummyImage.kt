package com.wldrmnd.thumbnail.api.data

import com.wldrmnd.thumbnail.api.models.ImagesModel

val dummyImage: ImagesModel
    get() {
        return ImagesModel(
            100,
            "demo.png",
            "/tmp/demo.png"
        )
    }
