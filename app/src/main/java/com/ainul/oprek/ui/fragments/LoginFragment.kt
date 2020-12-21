package com.ainul.oprek.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentLoginBinding
import com.ainul.oprek.ui.activities.MainActivity
import com.google.android.material.transition.MaterialSharedAxis

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding onclick
        binding = FragmentLoginBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Screen navigation animations
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        binding.buttonRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this.activity, MainActivity::class.java)
            this.startActivity(intent)
        }
    }
}