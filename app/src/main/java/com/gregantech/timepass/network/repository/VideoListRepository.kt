package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.VideoService
import com.gregantech.timepass.network.request.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

class VideoListRepository : TimePassBaseRepository() {
    private val videoService = RetrofitClient.retrofit.create(VideoService::class.java)

    suspend fun fetchCategoryVideoList(videoListRequest: VideoListRequest) = getResult {
        videoService.getVideo(videoListRequest)
    }

    suspend fun fetchUserVideoList(videoListRequest: UserVideoListRequest) = getResult {
        videoService.getUserVideo(videoListRequest)
    }

    suspend fun deleteUserPost(userPostDeleteRequest: UserPostDeleteRequest) = getResult {
        videoService.deleteUserPost(userPostDeleteRequest)
    }

    suspend fun fetchAllUserVideoList(videoListRequest: UserVideoListRequest) = getResult {
        videoService.getAllUserVideo(videoListRequest)
    }

    suspend fun setVideoLike(videoLikeRequest: VideoLikeRequest) = getResult {
        videoService.setAdminVideoLike(videoLikeRequest)
    }

    suspend fun setUserVideoLike(videoLikeRequest: VideoLikeRequest) = getResult {
        videoService.setUserVideoLike(videoLikeRequest)
    }

    suspend fun setUserFollow(userFollowRequest: UserFollowRequest) = getResult {
        videoService.setUserFollow(userFollowRequest)
    }

    suspend fun uploadVideo(
        userID: String,
        videoTitle: String,
        videoDescription: String,
        videoFile: File
    ) = getResult {
        videoService.uploadVideo(
            generateMultiPartString(userID),
            generateMultiPartString(videoTitle),
            generateMultiPartString(videoDescription),
            generateMultiPartVideo(videoFile)
        )
    }

    suspend fun uploadImage(
        userID: String,
        videoTitle: String,
        videoDescription: String,
        imageFile: File
    ) = getResult {
        videoService.uploadImage(
            generateMultiPartString(userID),
            generateMultiPartString(videoTitle),
            generateMultiPartString(videoDescription),
            generateMultiPartImage(imageFile)
        )
    }

    private fun generateMultiPartVideo(file: File): RequestBody {
        return RequestBody.create(MediaType.parse("video/mp4"), file)
    }

    private fun generateMultiPartImage(file: File): RequestBody {
        return RequestBody.create(MediaType.parse("image/jpeg"), file)
    }

    private fun generateMultiPartString(content: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), content)
    }

}