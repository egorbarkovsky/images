package com.wldrmnd.thumbnail.api.models

class ImageAvailableView(imagesModel: ImagesModel) {

    var id:Int = 0
    var path: String = ""
    var name:String=""

    init {
        this.id = imagesModel.id
        this.path = imagesModel.path
        this.name = imagesModel.name
    }
}