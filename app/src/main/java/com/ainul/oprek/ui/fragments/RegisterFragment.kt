package com.ainul.oprek.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentRegisterBinding
import com.ainul.oprek.ui.viewmodels.RegisterViewModel
import com.ainul.oprek.utils.showSnackBar
import com.google.android.material.transition.MaterialSharedAxis

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewmodel: RegisterViewModel by lazy {
        val application = requireNotNull(activity).application
        ViewModelProvider(
            this,
            RegisterViewModel.Factory(application)
        ).get(RegisterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewmodel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Screen navigation animations
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        }

        viewmodel.error.observe(viewLifecycleOwner, {
            val activityView = requireActivity().findViewById<View>(android.R.id.content)
            it?.let { message ->
                showSnackBar(activityView, message)
            }
        })

        viewmodel.successRegister.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(this.context, "Success created account", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.onFinish()
    }
}