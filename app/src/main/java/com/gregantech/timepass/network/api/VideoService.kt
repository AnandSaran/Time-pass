package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.request.*
import com.gregantech.timepass.network.response.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VideoService {

    @POST("videosList_api.php")
    suspend fun getVideo(@Body videoListRequest: VideoListRequest): Response<VideoListResponse>

    @POST("uservideosList_api.php")
    suspend fun getUserVideo(@Body videoListRequest: UserVideoListRequest): Response<VideoListResponse>

    @POST("videoList_api.php")
    suspend fun getFullScreenVideos(@Body videoListRequest: UserVideoListRequest): Response<VideoListResponse>

    @POST("uservideoList_api.php")
    suspend fun getFullScreenUserVideos(@Body videoListRequest: UserVideoListRequest): Response<VideoListResponse>

    @POST("homePageList_api.php")
    suspend fun getAllUserVideo(@Body videoListRequest: UserVideoListRequest): Response<VideoListResponse>

    @POST("postSearchList.php")
    suspend fun searchVideo(@Body searchVideoRequest: SearchVideosRequest): Response<VideoListResponse>

    @POST("videoLike_api.php")
    suspend fun setAdminVideoLike(@Body videoLikeRequest: VideoLikeRequest): Response<VideoLikeResponse>

    @POST("uservideoLike_api.php")
    suspend fun setUserVideoLike(@Body videoLikeRequest: VideoLikeRequest): Response<VideoLikeResponse>

    @POST("userFollow_api.php")
    suspend fun setUserFollow(@Body userFollowRequest: UserFollowRequest): Response<VideoFollowResponse>

    @Multipart
    @POST("userVideoUpload_api.php")
    suspend fun uploadVideo(
        @Part("userID") userID: RequestBody,
        @Part("videoTitle") videoTitle: RequestBody,
        @Part("videoDescription") videoDescription: RequestBody,
        @Part("videoFile\"; filename=\"videoFile.mp4") videoFile: RequestBody
    ): Response<VideoUploadResponse>

    @Multipart
    @POST("userImageUpload_api.php")
    suspend fun uploadImage(
        @Part("userID") userID: RequestBody,
        @Part("videoTitle") videoTitle: RequestBody,
        @Part("videoDescription") videoDescription: RequestBody,
        @Part("imageFile\"; filename=\"imageFile.png") imageFile: RequestBody
    ): Response<VideoUploadResponse>

    @POST("postDelete_api.php")
    suspend fun deleteUserPost(@Body userPostDeleteRequest: UserPostDeleteRequest): Response<UserPostDeleteResponse>

}