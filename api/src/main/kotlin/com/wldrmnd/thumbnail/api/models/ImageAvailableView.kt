package com.wldrmnd.thumbnail.api.models

class ImageAvailableView(imagesModel: ImagesModel,
                         var quatity: Int
) {

    var id: Int = 0
    var path: String = ""

    init {
        this.id = imagesModel.id
        this.path = imagesModel.path
    }
}
