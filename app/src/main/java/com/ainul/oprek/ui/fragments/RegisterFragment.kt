package com.ainul.oprek.ui.fragments

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.databinding.FragmentRegisterBinding
import com.ainul.oprek.ui.viewmodels.RegisterViewModel
import com.ainul.oprek.util.Constants
import com.ainul.oprek.util.Util
import com.google.android.material.transition.MaterialSharedAxis
import java.io.File
import java.io.IOException

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

        chooseImage(chooseImageDialog, view)
    }

    private fun chooseImage(dialog: AlertDialog, view: View) {
        val itemChooseImage: LinearLayout = view.findViewById(R.id.dialog_item_choose_image)
        val itemTakePhoto: LinearLayout = view.findViewById(R.id.dialog_item_take_photo)

        itemChooseImage.setOnClickListener {
            if (Util.isPermitted(this.requireContext(), permission.READ_EXTERNAL_STORAGE)) {
                selectImage()
            } else {
                requestPermissions(
                    arrayOf(permission.READ_EXTERNAL_STORAGE),
                    Constants.CHOOSE_IMAGE_REQUEST_CODE
                )
            }
            dialog.dismiss()
        }

        itemTakePhoto.setOnClickListener {
            if (Util.isPermitted(this.requireContext(), permission.CAMERA)) {
                // launch camera after permission permitted. Show dialog to allow the permission otherwise
                launchCamera()
            } else {
                requestPermissions(
                    arrayOf(permission.CAMERA),
                    Constants.LAUNCH_CAMERA_REQUEST_CODE
                )
            }
            dialog.dismiss()
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            val photoFile: File? = try {
                Util.createImageFile(this.requireActivity())
            } catch (e: IOException) {
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this.requireContext(),
                    "com.ainul.oprek.fileprovider",
                    it
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, Constants.LAUNCH_CAMERA_REQUEST_CODE)
            }
        }
    }

    /**
     * select image from Storage,
     *
     * it will launch activity to select image,
     * but of course the [permission.READ_EXTERNAL_STORAGE] need to be granted
     */
    private fun selectImage() {
        val selectImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        val packageManager = requireNotNull(activity).packageManager
        if (selectImageIntent.resolveActivity(packageManager) !== null) {
            startActivityForResult(selectImageIntent, Constants.CHOOSE_IMAGE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                Constants.CHOOSE_IMAGE_REQUEST_CODE -> selectImage()
                Constants.LAUNCH_CAMERA_REQUEST_CODE -> launchCamera()
                else -> Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE) {
            if (data != null) {
                val selectedImage: Uri? = data.data

                selectedImage?.let {
                    val path = Util.getSelectedImagePath(requireActivity().contentResolver, selectedImage)
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