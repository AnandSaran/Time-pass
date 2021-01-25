package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.CommentService
import com.gregantech.timepass.network.request.AddCommentRequest
import com.gregantech.timepass.network.request.CommentListRequest

class CommentListRepository : TimePassBaseRepository() {
    private val commentService = RetrofitClient.retrofit.create(CommentService::class.java)

    suspend fun getAdminVideoComment(request: CommentListRequest) = getResult {
        commentService.getAdminVideoComment(request)
    }

    suspend fun addAdminVideoComment(request: AddCommentRequest) = getResult {
        commentService.addAdminVideoComment(request)
    }

    suspend fun getUserVideoComment(request: CommentListRequest) = getResult {
        commentService.getUserVideoComment(request)
    }

    suspend fun addUserVideoComment(request: AddCommentRequest) = getResult {
        commentService.addUserVideoComment(request)
    }

}