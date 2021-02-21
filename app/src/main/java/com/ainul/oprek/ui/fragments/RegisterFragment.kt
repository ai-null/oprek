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
    private lateinit var viewmodel: RegisterViewModel

    private lateinit var imageDialogUtil: ImageDialogUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        // Screen navigation animations
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = resources.getInteger(R.integer.default_transitionDuration).toLong()
        }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = resources.getInteger(R.integer.default_transitionDuration).toLong()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val application = requireNotNull(activity).application
        viewmodel = ViewModelProvider(
            this,
            RegisterViewModel.Factory(application)
        ).get(RegisterViewModel::class.java)

        // define viewmodel and and lifecycleOwner
        binding.model = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        val activity = requireNotNull(activity)
        imageDialogUtil = ImageDialogUtil(activity, this)

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
                Constants.REQUEST_CODE_CHOOSE_IMAGE -> imageDialogUtil.selectImage()
                Constants.REQUEST_CODE_TAKE_PICTURE -> imageDialogUtil.launchCamera()
                else -> Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i(
            "result",
            "isDataNull: ${data !== null}, resultCode: $resultCode, reqCode: $requestCode "
        )

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_CHOOSE_IMAGE -> {
                    if (data != null) {
                        val selectedImage: Uri? = data.data

                        selectedImage?.let {
                            val path =
                                Util.getSelectedImagePath(
                                    requireActivity().contentResolver,
                                    selectedImage
                                )
                            viewmodel.updateProfilePicture(path)
                        }
                    }
                }

                Constants.REQUEST_CODE_TAKE_PICTURE -> {
                    val path = imageDialogUtil.currentPhotoPath

                    if (path.isNotBlank()) {
                        viewmodel.updateProfilePicture(path)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.onFinish()
    }
}