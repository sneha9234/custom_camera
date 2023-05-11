package com.example.customcamera.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.customcamera.viewModel.CaptureMultipleImageViewModel
import com.example.customcamera.R
import com.example.customcamera.databinding.FragmentCaptureMultipleImageBinding
import com.example.customcamera.model.SigningRequest
import com.example.customcamera.services.CameraService
import java.text.SimpleDateFormat
import java.util.*


class CaptureMultipleImageFragment : Fragment() {
    private var _binding: FragmentCaptureMultipleImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CaptureMultipleImageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureMultipleImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerImageClickedReceiver()
        initObserver()
        val serviceIntent = Intent(context, CameraService::class.java)
        serviceIntent.putExtra("multipleImages", "1")
        context?.startService(serviceIntent)
    }

    private fun initObserver() {
        viewModel.signatureHeaderLiveData.observe(viewLifecycleOwner) { res ->
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            viewModel.getTests(
                res["access-token"],
                res["uid"],
                res["client"], currentDate,
                null,
                "AAO",
                "NA",
                false
            )
        }

        viewModel.testsLiveData.observe(viewLifecycleOwner) { res ->
            if(res){
                binding.appCompatTextView.text = getString(R.string.test_done)
            }
            else{
                binding.appCompatTextView.text = getString(R.string.test_failed)
            }
        }
    }

    private val localBroadcastManager: LocalBroadcastManager?
        get() = context?.let { LocalBroadcastManager.getInstance(it) }

    private fun registerImageClickedReceiver() {
        val intent = IntentFilter("mutliPicClicked")
        localBroadcastManager?.registerReceiver(imageClickedReceiver, intent)
    }

    private var imageClickedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.appCompatTextView.text =
                getString(
                    R.string.capturing_image_for_exposure,
                    intent.getBundleExtra("bundle")?.getInt("exposure").toString()
                )

            if (intent.getBundleExtra("bundle")?.getInt("exposure") == 13) {
                binding.appCompatTextView.text = getString(R.string.sending_message)
                sendImage()
            }
        }
    }

    private fun sendImage() {
        viewModel.getSignature(
            SigningRequest(
                email = "amit_4@test.com",
                password = "12345678"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        localBroadcastManager?.unregisterReceiver(imageClickedReceiver)
        _binding = null
    }
}