package com.example.customcamera.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.customcamera.getMimeType
import com.example.customcamera.model.SigningRequest
import com.example.customcamera.network.APIClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CaptureMultipleImageViewModel(application: Application) : AndroidViewModel(application) {

    private val _signatureHeaderLiveData = MutableLiveData<Headers>()
    val signatureHeaderLiveData: LiveData<Headers> = _signatureHeaderLiveData

    private val _testsLiveData = MutableLiveData<Boolean>()
    val testsLiveData: LiveData<Boolean> = _testsLiveData

    fun getSignature(signingRequest: SigningRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = APIClient.api.getSignature(signingRequest).headers()
            _signatureHeaderLiveData.postValue(res)
        }
    }

    fun getTests(
        access_token: String?,
        uid: String?,
        client: String?,
        done_date: String?,
        pic: Uri? = null,
        batch_qr_code: String?,
        reason: String?,
        failure: Boolean?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val accessTokenForRequest = access_token
            val uidForRequest = uid
            val clientForRequest = client
            val doneDateForRequest = done_date?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val qrCodeForRequest = batch_qr_code?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reasonForRequest = reason?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val failureForRequest = failure?.toString()?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            var picForObject: MultipartBody.Part? = null
            pic?.let {
                val file = File(it.toString().removePrefix("file://"))
                val requestFile: RequestBody = file.asRequestBody(getMimeType(file).toMediaTypeOrNull())
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData("test[images_attributes][][pic]", file.name, requestFile)
                picForObject = body
            }
            try {
                val res = APIClient.api.getTests(
                    accessTokenForRequest,
                    uidForRequest,
                    clientForRequest,
                    doneDateForRequest,
                    qrCodeForRequest,
                    reasonForRequest,
                    failureForRequest,
                    picForObject
                )
                if (res.isSuccessful) {
                    _testsLiveData.postValue(true)
                } else {
                    _testsLiveData.postValue(false)
                }
            }
            catch(ex:Exception){
            }
        }
    }

}