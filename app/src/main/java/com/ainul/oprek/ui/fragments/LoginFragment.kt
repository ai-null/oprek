package com.ainul.oprek.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentLoginBinding
import com.ainul.oprek.ui.activities.MainActivity
import com.ainul.oprek.ui.viewmodels.LoginViewModel
import com.ainul.oprek.util.Util
import com.google.android.material.transition.MaterialSharedAxis
import com.ainul.oprek.ui.viewmodels.LoginViewModel.Companion.AuthenticationState

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewmodel: LoginViewModel by lazy {
        val application = requireNotNull(activity).application
        ViewModelProvider(
            this,
            LoginViewModel.Factory(application)
        ).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        // Screen navigation animations
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Error watcher
        viewmodel.error.observe(viewLifecycleOwner, {
            it?.let { message ->
                Util.showSnackBar(view, message)
            }
        })

        // Authentication state, if true go to main activity
        // destroy current activity
        viewmodel.authenticationState.observe(viewLifecycleOwner, {
            if (it == AuthenticationState.AUTHENTICATED) {
                // proceed to go to main activity
                val intent = Intent(this.activity, MainActivity::class.java)
                this.startActivity(intent)

                // tasks done, finish activity
                activity?.finish()
            }
        })

        // navigate to register screen
        binding.buttonRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}