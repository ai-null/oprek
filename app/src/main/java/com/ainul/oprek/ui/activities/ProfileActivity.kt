package com.ainul.oprek.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityProfileBinding
import com.ainul.oprek.databinding.BottomSheetEditDataBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel
import com.ainul.oprek.util.Constants
import com.ainul.oprek.util.ImageDialogUtil
import com.ainul.oprek.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ainul.oprek.ui.viewmodels.MainViewModel.Companion.DataToUpdate

class ProfileActivity : AppCompatActivity() {

    private val viewmodel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(this.application)
        ).get(MainViewModel::class.java)
    }
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        // set custom toolbar
        val toolbar: Toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
    }

    override fun onStart() {
        super.onStart()
        viewmodel.logoutState.observe(this, {
            if (it) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewmodel.dataUpdated.observe(this, {
            if (it) setResult(Constants.RESULT_CODE_UPDATED)
        })

        // Edit username
        binding.inputUsernameLayout.setEndIconOnClickListener {
            showBottomDialog(DataToUpdate.USERNAME)
        }

        // Edit company
        binding.inputCompanyLayout.setEndIconOnClickListener {
            showBottomDialog(DataToUpdate.COMPANY)
        }

        // Edit profile picture
        binding.editPictureFab.setOnClickListener {
            showChooseImageDialog()
        }
    }


    private val dialog: BottomSheetDialog by lazy {
        BottomSheetDialog(this, R.style.AppTheme_BottomSheetDialog)
    }

    private fun showBottomDialog(dataToUpdate: DataToUpdate) {
        val dialogBinding = BottomSheetEditDataBinding.inflate(layoutInflater)
        val inputField = dialogBinding.inputField
        dialog.setContentView(dialogBinding.root)

        val isUsername = dataToUpdate == DataToUpdate.USERNAME

        dialogBinding.viewmodel = viewmodel
        dialogBinding.isUsername = isUsername

        dialog.window?.let {
            dialog.show()
            inputField.requestFocus()
        }

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.buttonSave.setOnClickListener {
            val content: String = inputField.text.toString()
            if (content.isBlank()) {
                val messageRes = resources.getString(R.string.dialog_alert_message)
                val placeholder = if (isUsername) "User" else "Company"
                val message = String.format(messageRes, placeholder)

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                viewmodel.updateData(dataToUpdate, content).also {
                    dialog.dismiss()
                }
            }
        }
    }

    // Image handle utility
    private val imageDialogUtil: ImageDialogUtil by lazy {
        ImageDialogUtil(this)
    }

    private fun showChooseImageDialog() {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(
            R.layout.dialog_choose_image,
            findViewById(R.id.choose_image_container)
        )

        // set view & create dialog
        dialog.setView(view)
        val chooseImageDialog = dialog.create()

        // show dialog
        chooseImageDialog.window?.run {
            setBackgroundDrawable(ColorDrawable(0))
            chooseImageDialog.show()
        }

        imageDialogUtil.chooseImage(chooseImageDialog, view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_CHOOSE_IMAGE -> {
                    if (data != null) {
                        val selectedImage: Uri? = data.data

                        selectedImage?.let {
                            val path = Util.getSelectedImagePath(contentResolver, selectedImage)
                            viewmodel.updateData(DataToUpdate.PROFILE_PICTURE, path)
                        }
                    }
                }

                Constants.REQUEST_CODE_TAKE_PICTURE -> {
                    val path = imageDialogUtil.currentPhotoPath

                    if (path.isNotBlank()) {
                        viewmodel.updateData(DataToUpdate.PROFILE_PICTURE, path)
                    }
                }
            }
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
                Constants.REQUEST_CODE_CHOOSE_IMAGE -> imageDialogUtil.selectImage()
                Constants.REQUEST_CODE_TAKE_PICTURE -> imageDialogUtil.launchCamera()
                else -> Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}