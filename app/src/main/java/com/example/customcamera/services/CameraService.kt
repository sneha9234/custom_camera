package com.example.customcamera.services

import android.app.Service
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CameraService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    var camera: Camera? = null

    init {
        val cameraInfo = CameraInfo()
        val frontCamera = 1
        Camera.getCameraInfo(frontCamera, cameraInfo)
        camera = try {
            Camera.open(frontCamera)
        } catch (e: RuntimeException) {
            null
        }
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getStringExtra("multipleImages") == "1") {
            scope.launch {
                try {
                    captureMultiplePhoto()
                } catch (ex: Exception) {

                }
            }
        } else {
            scope.launch {
                try {
                    capturePhoto()
                } catch (ex: Exception) {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun capturePhoto() {
        try {
            if (null == camera) {
            } else {
                try {
                    val camParams: Camera.Parameters? = camera?.parameters
                    if (camParams != null) {
                        val supportedIsoValues = camParams["iso-values"]
                        val supportedFocusValues = camParams["focus-mode-values"]
                        if (supportedIsoValues != null && supportedIsoValues.contains("100")) {
                            if (camParams.get("iso") != null)
                                camParams.set("iso", "100")
                        }
                        if (supportedFocusValues != null && supportedFocusValues.contains("1")) {
                            if (camParams.get("focus-mode") != null)
                                camParams.set("focus-mode", "100")
                        }
                        camera?.parameters = camParams
                    }

                    camera?.setPreviewTexture(SurfaceTexture(0))
                    camera?.startPreview()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                camera?.takePicture(null, null, PictureCallback { data, camera ->
                    val pictureFileDir = File(applicationContext.filesDir.path)
                    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                        pictureFileDir.mkdirs()
                    }
                    val dateFormat = SimpleDateFormat("yyyymmddhhmmss")
                    val date: String = dateFormat.format(Date())
                    val photoFile = "CustomCamera_$date.jpg"
                    val filename: String =
                        pictureFileDir.path + File.separator.toString() + photoFile
                    val mainPicture = File(filename)
                    try {
                        val fos = FileOutputStream(mainPicture)
                        fos.write(data)
                        fos.close()
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("picClicked"))
                    } catch (error: Exception) {
                    }
                })
            }
        } catch (e: Exception) {
        }
    }

    private suspend fun captureMultiplePhoto() {
        try {
            if (null == camera) {
            } else {
                val camParams: Camera.Parameters? = camera?.parameters
                val supportedIsoValues = camParams?.get("iso-values")
                val supportedFocusValues = camParams?.get("focus-mode-values")
                val supportedExposure = camParams?.get("exposure-compensation")
                val supportedMaxExposure = camParams?.get("max-exposure-compensation")
                val supportedMinExposure = camParams?.get("min-exposure-compensation")
                if (supportedIsoValues != null && supportedIsoValues.contains("100")) {
                    if (camParams.get("iso") != null)
                        camParams.set("iso", "100")
                }
                if (supportedFocusValues != null && supportedFocusValues.contains("1")) {
                    if (camParams.get("focus-mode") != null)
                        camParams.set("focus-mode", "100")
                }
                if (supportedMaxExposure != null) {
                        camParams.set("supportedMaxExposure", "12")
                }
                if (supportedMinExposure != null) {
                        camParams.set("supportedMinExposure", "-12")
                }
                for (i in -12..13) {
                    if(i==13){
                        val intent = Intent("mutliPicClicked")
                        val b = Bundle()
                        b.putInt("exposure", i)
                        intent.putExtra("bundle", b)
                        LocalBroadcastManager.getInstance(this)
                            .sendBroadcast(intent)
                        return
                    }
                    try {
                        if (supportedExposure != null) {
                            camParams.set("", i)
                        }
                        camera?.parameters = camParams
                        camera?.setPreviewTexture(SurfaceTexture(0))
                        camera?.startPreview()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    camera?.takePicture(null, null, PictureCallback { data, camera ->
                        val pictureFileDir = File(applicationContext.filesDir.path)
                        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                            pictureFileDir.mkdirs()
                        }
                        val dateFormat = SimpleDateFormat("yyyymmddhhmmss")
                        val date: String = dateFormat.format(Date())
                        val photoFile = "CustomCamera_$date.jpg"
                        val filename: String =
                            pictureFileDir.path + File.separator.toString() + photoFile
                        val mainPicture = File(filename)
                        try {
                            val fos = FileOutputStream(mainPicture)
                            fos.write(data)
                            fos.close()
                            val intent = Intent("mutliPicClicked")
                            val b = Bundle()
                            b.putInt("exposure", i)
                            intent.putExtra("bundle", b)
                            LocalBroadcastManager.getInstance(this)
                                .sendBroadcast(intent)
                        } catch (error: Exception) {
                        }
                    })
                    delay(2000)
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        // camera?.release()
    }
}