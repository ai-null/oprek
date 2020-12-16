package com.ainul.oprek.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentLoginBinding
import com.ainul.oprek.ui.activities.DashboardActivity
import androidx.transition.Transition
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Screen navigation animations
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        // Binding onclick
        val binding = FragmentLoginBinding.inflate(inflater)
        binding.buttonRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this.activity, DashboardActivity::class.java)
            this.startActivity(intent)
        }

        return binding.root
    }
}