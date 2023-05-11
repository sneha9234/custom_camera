package com.example.customcamera.model

import com.google.gson.annotations.SerializedName

data class SigningResponse(
    @SerializedName("id")
    val id: Int?=null,
    @SerializedName("email")
    val email: String?=null,
    @SerializedName("onboarded")
    val onboarded: Boolean?=false,
    @SerializedName("access-token")
    val access_token: String?=null,
    @SerializedName("uid")
    val uid: String?=null,
    @SerializedName("client")
    val client: String?=null
)