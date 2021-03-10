package com.ainul.oprek.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityAddProjectBinding
import com.ainul.oprek.ui.viewmodels.AddProjectViewModel
import com.ainul.oprek.util.Constants
import com.ainul.oprek.util.ImageDialogUtil
import com.ainul.oprek.util.Util
import com.ainul.oprek.util.Util.getSelectedImagePath
import java.text.SimpleDateFormat
import java.util.*

class AddProjectActivity : AppCompatActivity() {

    // viewmodel & binding to handle UI-related data and actions
    private lateinit var binding: ActivityAddProjectBinding
    private lateinit var viewmodel: AddProjectViewModel

    // set if only the activity started from MainActivity
    private var projectId: Long? = null

    // Image handle utility
    private val imageDialogUtil: ImageDialogUtil by lazy {
        ImageDialogUtil(this)
    }

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
     * and the actions of this dialog is handled by [ImageDialogUtil]
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
                else -> Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            when (requestCode) {
                Constants.REQUEST_CODE_CHOOSE_IMAGE -> {
                    if (data != null) {
                        val selectedImage: Uri? = data.data

                        selectedImage?.let {
                            val path = getSelectedImagePath(contentResolver, selectedImage)
                            viewmodel.updateCurrentPhotoPath(path)
                        }
                    }
                }

                Constants.REQUEST_CODE_TAKE_PICTURE -> {
                    val path = imageDialogUtil.currentPhotoPath

                    if (path.isNotBlank()) {
                        viewmodel.updateCurrentPhotoPath(path)
                    }
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