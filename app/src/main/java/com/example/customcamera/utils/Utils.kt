package com.example.customcamera

import android.content.Context
import android.content.pm.PackageManager
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import com.example.customcamera.utils.DebouncedOnClickListener
import java.io.File
import java.net.URLConnection


fun isValidString(value: String?): Boolean {
    return value != null && value.isNotEmpty() && value != "null"
}

fun isValidEmail(email: String?): Boolean {
    return if(email!=null){
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    else{
        false
    }
}

inline fun View.debouncedOnClick(debounceTill: Long = 1000, crossinline onClick: (v: View) -> Unit) {
    this.setOnClickListener(object : DebouncedOnClickListener(debounceTill) {
        override fun onDebouncedClick(v: View) {
            onClick(v)
        }
    })
}

fun NavController.navigateSafely(directions : NavDirections){
    try {
        val destinationId  = currentDestination?.getAction(directions.actionId)?.destinationId?:-1

        currentDestination?.let { node ->
            val currentNode = when (node) {
                is NavGraph -> node
                else -> node.parent
            }
            if (destinationId != -1) {
                currentNode?.findNode(destinationId)?.let { navigate(directions) }
            }
        }
    }catch (e : Exception){
        e.printStackTrace()
    }
}

fun arePermissionsAlreadyAccepted(mContext: Context, permissions: Array<String>): Boolean {
    var allPermissionGranted = true
    permissions.forEach { permission ->
        val permissionState = ContextCompat.checkSelfPermission(mContext, permission);
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            allPermissionGranted = false
        }
    }
    return allPermissionGranted
}

fun getMimeType(file: File): String {
    return URLConnection.guessContentTypeFromName(file.name)
}