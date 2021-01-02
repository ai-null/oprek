package com.ainul.oprek.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.databinding.FragmentRegisterBinding
import com.ainul.oprek.ui.viewmodels.RegisterViewModel
import com.ainul.oprek.utils.Util
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
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewmodel

        // Screen navigation animations
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Error snackBar handler, shows on state update
        viewmodel.error.observe(viewLifecycleOwner, {
            it?.let { message ->
                Util.showSnackBar(view, message)
            }
        })

        // register state watcher, shows a toast when user registered successfully
        viewmodel.successRegister.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(this.context, "Register Success", Toast.LENGTH_SHORT).show()
                Log.i("Register:", "success")
            }
        })

        // navigateBack state watcher, delay 700ms then navigateBack after toast gone
        viewmodel.navigateBack.observe(viewLifecycleOwner, {
            if (it) {
                this.findNavController().navigateUp()
                viewmodel.navigateComplete()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.onFinish()
    }
}