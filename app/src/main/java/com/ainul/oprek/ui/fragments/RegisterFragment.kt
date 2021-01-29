package com.ainul.oprek.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentRegisterBinding
import com.ainul.oprek.ui.viewmodels.RegisterViewModel
import com.ainul.oprek.util.*
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

    private lateinit var imageDialogUtil: ImageDialogUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewmodel

        imageDialogUtil = ImageDialogUtil(this.requireActivity())

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

        // show dialog choose image profile
        binding.addProfileImage.setOnClickListener {
            showDialogChooseImage()
        }
    }

    private fun showDialogChooseImage() {
        val dialog = AlertDialog.Builder(this.context)
        val view = layoutInflater.inflate(
            R.layout.dialog_choose_image,
            binding.root.findViewById(R.id.choose_image_container)
        )

        dialog.setView(view)
        val chooseImageDialog = dialog.create()

        chooseImageDialog.window?.run {
            setBackgroundDrawable(ColorDrawable(0))
            chooseImageDialog.show()
        }

        imageDialogUtil.chooseImage(chooseImageDialog, view)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                Constants.CHOOSE_IMAGE_REQUEST_CODE -> imageDialogUtil.selectImage(this.requireActivity())
                Constants.LAUNCH_CAMERA_REQUEST_CODE -> imageDialogUtil.launchCamera(requireActivity())
                else -> Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK &&
            requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE ||
            requestCode == Constants.LAUNCH_CAMERA_REQUEST_CODE
        ) {

            Log.i("hey", "$data")

            if (data != null) {
                val selectedImage: Uri? = data.data

                Log.i("hey2", "$data")

                selectedImage?.let {
                    val path =
                        Util.getSelectedImagePath(requireActivity().contentResolver, selectedImage)
                    Log.i("hey3", path)
                    viewmodel.updateProfilePicture(path)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.onFinish()
    }
}