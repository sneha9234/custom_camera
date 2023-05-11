package com.example.customcamera.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

open class SharedPreferences(context: Context) {
    val prefs: SharedPreferences =
        context.getSharedPreferences(
            SharedPreferences::class.java.simpleName,
            Activity.MODE_PRIVATE
        )
    val editor: SharedPreferences.Editor = prefs.edit()

    var fullName: String?
        get() = prefs.getString("fullName", "")
        set(token) = editor.putString("fullName", token).apply()

    var emailId: String?
        get() = prefs.getString("emailId", "")
        set(token) = editor.putString("emailId", token).apply()
}