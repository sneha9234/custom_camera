package com.example.customcamera.viewModel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CaptureSingleImageViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var countDownTimer: CountDownTimer

    private val _countertime: MutableLiveData<Int> =
        MutableLiveData<Int>()
    val countertime: LiveData<Int> = _countertime

    fun startTimer() {
        var i = 300
        countDownTimer = object : CountDownTimer(300000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                i -= 5
                _countertime.value = i
            }

            override fun onFinish() {
                _countertime.value = -1
            }
        }
        countDownTimer.start()
    }

    override fun onCleared() {
        super.onCleared()
        if(::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}