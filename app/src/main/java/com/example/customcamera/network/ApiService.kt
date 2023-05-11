package com.example.customcamera.network

import com.example.customcamera.model.SigningRequest
import com.example.customcamera.model.SigningResponse
import com.example.customcamera.utils.Constants.SIGNING_API
import com.example.customcamera.utils.Constants.TESTS_API
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type:application/json")
    @POST(SIGNING_API)
    suspend fun getSignature(
        @Body body: SigningRequest
    ): Response<SigningResponse>

    @Multipart
    @POST(TESTS_API)
    suspend fun getTests(
        @Header("access-token") access_token: String?,
        @Header("uid") uid: String?,
        @Header("client") client: String?,
        @Part("test[done_date]") done_date: RequestBody?=null,
        @Part("test[batch_qr_code]") batch_qr_code: RequestBody?=null,
        @Part("test[reason]") reason: RequestBody?=null,
        @Part("test[failure]") failure: RequestBody?=null,
        @Part pic: MultipartBody.Part?=null
        ):Response<Any>
}