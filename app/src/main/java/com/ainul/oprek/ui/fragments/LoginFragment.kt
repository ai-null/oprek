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
    private lateinit var viewmodel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val application = requireNotNull(activity).application
        viewmodel = ViewModelProvider(
            this,
            LoginViewModel.Factory(application)
        ).get(LoginViewModel::class.java)

        // define viewmodel and lifecycleOwner
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

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
            navigateToRegister()
        }
    }

    private fun navigateToRegister() {
        // Screen navigation animations
        val defaultTransitionDuration = resources.getInteger(R.integer.default_transitionDuration).toLong()

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = defaultTransitionDuration
        }

        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = defaultTransitionDuration
        }

        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}