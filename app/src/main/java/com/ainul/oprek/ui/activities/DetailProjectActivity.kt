package com.ainul.oprek.ui.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityDetailProjectBinding
import com.ainul.oprek.ui.viewmodels.DetailViewModel
import com.ainul.oprek.util.Constants
import com.ainul.oprek.util.Constants.Status
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProjectBinding
    private lateinit var viewmodel: DetailViewModel
    private var projectId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // defines data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_project)

        // get the userId, assign it to the viewmodel
        projectId = intent.extras!!.getLong(Constants.PROJECT_ID)
        viewmodel = ViewModelProvider(
            this,
            DetailViewModel.Factory(application, projectId)
        ).get(DetailViewModel::class.java)

        // set the viewmodel in the xml, and attach the activity as lifecycleOwner
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this
    }

    override fun onStart() {
        super.onStart()

        // finish the activity and back to the previous screen whenever `navigateBack` set to true
        viewmodel.navigateBack.observe(this, {
            if (it) {
                finish()
            }
        })

        binding.updateStatusFab.setOnClickListener {
            showUpdateStatusDialog()
        }
    }

    private fun showUpdateStatusDialog() {
        val dialog = BottomSheetDialog(this, R.style.AppTheme_BottomSheetDialog)
        val view = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_edit_status,
            findViewById(R.id.dialog_container)
        )

        // set view to dialog & create dialog from the inflated layout
        dialog.setContentView(view)

        // show dialog
        dialog.window?.run {
            setBackgroundDrawable(ColorDrawable(0))
            dialog.show()
        }

        updateStatus(dialog, view)
    }

    private fun updateStatus(dialog: BottomSheetDialog, view: View) {
        val itemDone: LinearLayout = view.findViewById(R.id.dialog_item_done)
        val itemCancel: LinearLayout = view.findViewById(R.id.dialog_item_cancel)
        val itemProgress: LinearLayout = view.findViewById(R.id.dialog_item_progress)

        itemDone.setOnClickListener {
            viewmodel.updateStatus(Status.DONE.value)
            dialog.dismiss()
        }

        itemCancel.setOnClickListener {
            viewmodel.updateStatus(Status.CANCEL.value)
            dialog.dismiss()
        }

        itemProgress.setOnClickListener {
            viewmodel.updateStatus(Status.PROGRESS.value)
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_UPDATE_PROJECT && resultCode == Constants.PROJECT_UPDATED) {
            viewmodel.refresh() // refresh data after update
        }
    }

    // Override to create options for edit and delete project
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_project_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Override to defines method for the clicked menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_edit -> {
                val intent = Intent(this, AddProjectActivity::class.java)
                intent.putExtra(Constants.PROJECT_ID, projectId) // send project_id to update

                // start activity for updating project
                startActivityForResult(intent, Constants.REQUEST_CODE_UPDATE_PROJECT)
            }
            R.id.item_delete -> {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
                viewmodel.onDelete()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Override method to show back arrow
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}