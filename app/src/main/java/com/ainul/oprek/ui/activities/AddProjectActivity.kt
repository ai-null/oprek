package com.ainul.oprek.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityAddProjectBinding
import com.ainul.oprek.ui.viewmodels.AddProjectViewModel
import com.ainul.oprek.utils.Util

class AddProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProjectBinding
    private lateinit var viewmodel: AddProjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // defines viewmodel and data-binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_project)
        viewmodel = ViewModelProvider(this, AddProjectViewModel.Factory(application)).get(
            AddProjectViewModel::class.java
        )

        // set lifecycleOwner so the activity can notice state change,
        // and attach the viewmodel to the UI
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel

        // when add project compiled successfully, proceed to finish the activity
        viewmodel.successAddProject.observe(this, {
            if (it) {
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
}