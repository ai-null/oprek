package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityDetailProjectBinding
import com.ainul.oprek.ui.viewmodels.DetailViewModel

private const val UPDATE_PROJECT_REQUEST_CODE = 4

class DetailProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProjectBinding
    private lateinit var viewmodel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // defines data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_project)

        // get the userId, assign it to the viewmodel
        val projectId = intent.extras!!.get("PROJECT_ID")
        viewmodel = ViewModelProvider(
            this,
            DetailViewModel.Factory(application, projectId.toString().toLong())
        ).get(DetailViewModel::class.java)

        // set the viewmodel in the xml, and attach the activity as lifecycleOwner
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this

        viewmodel.navigateBack.observe(this, {
            if (it) {
                finish()
            }
        })
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
                startActivityForResult(intent, UPDATE_PROJECT_REQUEST_CODE)
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