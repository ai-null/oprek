package com.ainul.oprek.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.Manifest.permission
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityAddProjectBinding
import com.ainul.oprek.ui.viewmodels.AddProjectViewModel
import com.ainul.oprek.utils.Constants
import com.ainul.oprek.utils.Util
import com.ainul.oprek.utils.Util.Companion.createImageFile
import com.ainul.oprek.utils.Util.Companion.getSelectedImagePath
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProjectBinding
    private lateinit var viewmodel: AddProjectViewModel
    private var projectId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // attach the activity to the xml using data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_project)

        // set navigate-up to actionbar, called this the moment layout is set
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent?.hasExtra(Constants.PROJECT_ID) == true) {
            // set actionbar title to be "Update project" when starts from `DetailActivity`
            supportActionBar?.title = resources.getString(R.string.update_project)

            // set projectId
            projectId = intent.extras?.getLong(Constants.PROJECT_ID)
        }

        // defines viewmodel
        viewmodel = ViewModelProvider(
            this,
            AddProjectViewModel.Factory(application, projectId)
        ).get(AddProjectViewModel::class.java)

        // set lifecycleOwner so the activity can notice state change,
        // and attach the viewmodel to the UI
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
    }

    override fun onStart() {
        super.onStart()

        // when add project compiled successfully, proceed to finish the activity
        viewmodel.successAddProject.observe(this, {
            it?.let {
                if (it == Constants.PROJECT_UPDATED) {
                    // to tell parent-activity this is an updated data
                    setResult(Constants.PROJECT_UPDATED)
                }

                finish()
            }
        })

        // show snackBar on error
        viewmodel.error.observe(this, {
            it?.let {
                Util.showSnackBar(binding.root, it)
            }
        })

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val format = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
                viewmodel.dueDate = format.format(cal.time).toString()
            }

        binding.dueClicklistener.setOnClickListener {
            Log.i("working", "yes")
            DatePickerDialog(
                this@AddProjectActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.addDeviceImage.setOnClickListener {
            showChooseImageDialog()
        }
    }

    /**
     * show choose image dialog
     *
     * this method purposed only to show the dialog.
     * The dialog is inflated from [R.layout.dialog_choose_image],
     * and the actions of this dialog is handled by [chooseImage] method below
     */
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

        chooseImage(chooseImageDialog, view)
    }

    /**
     * dialog actions,
     * any clickListener from dialog handled here
     *
     * @param dialog [AlertDialog] to dismiss after clicked
     * @param view [View] to get item- each element from [R.layout.dialog_choose_image]
     */
    private fun chooseImage(dialog: AlertDialog, view: View) {
        val itemChooseImage: LinearLayout = view.findViewById(R.id.dialog_item_choose_image)
        val itemTakePhoto: LinearLayout = view.findViewById(R.id.dialog_item_take_photo)

        itemChooseImage.setOnClickListener {
            if (Util.isPermitted(this, permission.READ_EXTERNAL_STORAGE)) {
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
            if (Util.isPermitted(this, permission.CAMERA)) {
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

    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            val photoFile: File? = try {
                createImageFile(this)
            } catch (e: IOException) {
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
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
                else -> Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE) {
            if (data != null) {
                val selectedImage: Uri? = data.data

                selectedImage?.let {
                    val path = getSelectedImagePath(contentResolver, selectedImage)
                    viewmodel.updateCurrentPhotoPath(path)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // onNavigateUp / actionBar back button
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}