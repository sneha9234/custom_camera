package com.example.customcamera.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.customcamera.*
import com.example.customcamera.databinding.FragmentHomeBinding
import com.example.customcamera.utils.SharedPreferences

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val preferences by lazy { SharedPreferences(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        binding.btnTakeTest.debouncedOnClick {
            if(isValidString(binding.etName.text.toString()) && isValidEmail(binding.etEmail.text.toString())){
                saveDataAndGo()
            }
            else{
                Toast.makeText(context, getString(R.string.enter_valid_warning), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDataAndGo() {
        preferences.emailId = binding.etEmail.text.toString()
        preferences.fullName = binding.etName.text.toString()
        findNavController().navigateSafely(HomeFragmentDirections.actionNavigationHomeFragmentToCaptureSingleImageFragment())
    }

    private fun initView() {
        if(isValidString(preferences.fullName))
            binding.etName.setText(preferences.fullName)

        if(isValidString(preferences.emailId))
            binding.etEmail.setText(preferences.emailId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}