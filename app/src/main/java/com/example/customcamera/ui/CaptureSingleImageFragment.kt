package com.example.customcamera.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.example.customcamera.R
import com.example.customcamera.viewModel.CaptureSingleImageViewModel
import com.example.customcamera.arePermissionsAlreadyAccepted
import com.example.customcamera.databinding.FragmentCaptureSingleImageBinding
import com.example.customcamera.navigateSafely
import com.example.customcamera.services.CameraService
import com.github.florent37.runtimepermission.kotlin.askPermission

class CaptureSingleImageFragment : Fragment() {
    private var _binding: FragmentCaptureSingleImageBinding? = null
    private val binding get() = _binding!!
    var cam: Camera? = null
    private val viewModel: CaptureSingleImageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureSingleImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForPermission()
        initObserver()
        registerImageClickedReceiver()
    }

    private fun initObserver() {
        viewModel.countertime.observe(viewLifecycleOwner) { counterTime ->
            if (counterTime > 0) {
                binding.pgBar.progress = counterTime
                binding.txtTimer.text = counterTime.toString()
            } else {
                findNavController().navigateSafely(CaptureSingleImageFragmentDirections.actionNavigationCaptureSingleImageFragmentToCaptureMultipleImageFragment())
            }
        }
    }

    override fun onDestroyView() {
        localBroadcastManager?.unregisterReceiver(imageClickedReceiver)
        _binding = null
        super.onDestroyView()
    }

    private fun checkForPermission() {
        context?.let { mContext ->
            if (arePermissionsAlreadyAccepted(
                    mContext,
                    arrayOf(Manifest.permission.CAMERA)
                )
            ) {
                context?.startService(Intent(context, CameraService::class.java))
            } else {
                askPermission(Manifest.permission.CAMERA) {
                    checkForPermission()
                }.onDeclined { _ ->
                    Toast.makeText(context, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val localBroadcastManager: LocalBroadcastManager?
        get() = context?.let { LocalBroadcastManager.getInstance(it) }

    private fun registerImageClickedReceiver() {
        val intent = IntentFilter("picClicked")
        localBroadcastManager?.registerReceiver(imageClickedReceiver, intent)
    }


    private var imageClickedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.pgBar.visibility = View.VISIBLE
            binding.pgBar.max = 300
            viewModel.startTimer()
        }
    }
}
