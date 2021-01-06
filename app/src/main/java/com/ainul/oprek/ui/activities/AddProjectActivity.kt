package com.ainul.oprek.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityAddProjectBinding
import com.ainul.oprek.ui.viewmodels.AddProjectViewModel
import com.ainul.oprek.utils.Constants
import com.ainul.oprek.utils.Util

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // onNavigateUp / actionBar back button
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}