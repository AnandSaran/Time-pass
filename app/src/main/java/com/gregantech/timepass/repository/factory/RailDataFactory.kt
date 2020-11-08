package com.gregantech.timepass.repository.factory

import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.model.RailItemTypeTwoModel

class RailDataFactory {
    private val categoryData = arrayListOf<RailBaseItemModel>()
    private val categoryVideoData = arrayListOf<RailBaseItemModel>()

    init {
        categoryData.addAll(generateCategoryList())
        categoryVideoData.addAll(generateCategoryVideoList())
    }

    private fun generateCategoryList(): Collection<RailItemTypeOneModel> {
        return arrayListOf(
            RailItemTypeOneModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg"
            ), RailItemTypeOneModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png"
            ),
            RailItemTypeOneModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg"
            ), RailItemTypeOneModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg"
            ),
            RailItemTypeOneModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg"
            ),
            RailItemTypeOneModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png"
            ), RailItemTypeOneModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg"
            ), RailItemTypeOneModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg"
            ), RailItemTypeOneModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg"
            ),
            RailItemTypeOneModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg"
            ), RailItemTypeOneModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png"
            ), RailItemTypeOneModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg"
            ), RailItemTypeOneModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png"
            )
        )
    }

    private fun generateCategoryVideoList(): Collection<RailBaseItemModel> {
        return arrayListOf(
            RailItemTypeTwoModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ), RailItemTypeTwoModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ), RailItemTypeTwoModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ), RailItemTypeTwoModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ), RailItemTypeTwoModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "1",
                "Trending",
                "https://pyxis.nymag.com/v1/imgs/536/b41/8734f5337be63d0a434bac9bea0fa0143d-21-trending.rsocial.w1200.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ), RailItemTypeTwoModel(
                "2",
                "Hot",
                "https://static.wixstatic.com/media/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png/v1/fill/w_780,h_460,al_c/ecf19d_28d573cd414f4e7c8a463098abcd6c64~mv2.png",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            ),
            RailItemTypeTwoModel(
                "2",
                "Traditional",
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX32457572.jpg",
                video = "http://www.adroitsolutionz.com/video-test-2.mp4",
                width = "640.0",
                height = "800.0",
                isLiked = true
            )
        )
    }

    internal fun getCategoryList(): ArrayList<RailBaseItemModel> {
        return categoryData
    }

    internal fun getCategoryVideoList(): ArrayList<RailBaseItemModel> {
        return categoryVideoData
    }

}