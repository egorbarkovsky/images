package com.wldrmnd.thumbnail.api.models

import com.wldrmnd.thumbnail.api.data.dummyImage
import org.junit.jupiter.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ImagesModelTest : Spek({

    lateinit var imagesModel : ImagesModel

    val dummyImage = dummyImage

    beforeEachGroup {
        imagesModel = ImagesModel( 100,"demo.png","/tmp/demo.png")
    }

    describe("is available") {
        beforeEachTest {
            imagesModel = imagesModel.copy(id = 100)
        }
        it("should return true") {
            assertEquals(imagesModel,dummyImage)
        }
    }
})