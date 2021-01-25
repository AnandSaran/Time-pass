package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.request.AddCommentRequest
import com.gregantech.timepass.network.request.CommentListRequest
import com.gregantech.timepass.network.response.AddVideoCommentResponse
import com.gregantech.timepass.network.response.comments.CommentListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentService {

    @POST("videoComments_api.php")
    suspend fun getAdminVideoComment(@Body commentListRequest: CommentListRequest): Response<CommentListResponse>

    @POST("uservideoComments_api.php")
    suspend fun getUserVideoComment(@Body commentListRequest: CommentListRequest): Response<CommentListResponse>

    @POST("videoAddComments_api.php")
    suspend fun addAdminVideoComment(@Body commentListRequest: AddCommentRequest): Response<AddVideoCommentResponse>

    @POST("uservideoAddComments_api.php")
    suspend fun addUserVideoComment(@Body commentListRequest: AddCommentRequest): Response<AddVideoCommentResponse>
}