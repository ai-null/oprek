package com.ainul.oprek.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        binding.buttonRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        return binding.root
    }
}