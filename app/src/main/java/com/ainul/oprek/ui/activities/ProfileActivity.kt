package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityProfileBinding
import com.ainul.oprek.databinding.BottomSheetEditDataBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

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

        binding.inputUsernameLayout.setEndIconOnClickListener {
            showBottomDialog(true)
        }

        binding.inputCompanyLayout.setEndIconOnClickListener {
            showBottomDialog(false)
        }
    }


    private val dialog: BottomSheetDialog by lazy {
        BottomSheetDialog(this, R.style.AppTheme_BottomSheetDialog)
    }

    private fun showBottomDialog(isUsername: Boolean) {
        val dialogBinding = BottomSheetEditDataBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.viewmodel = viewmodel
        dialogBinding.isUsername = isUsername

        dialog.window?.run {
            dialog.show()
            dialogBinding.inputField.requestFocus()
        }

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.buttonSave.setOnClickListener {
            val inputField = dialogBinding.inputField.text.toString()
            if (inputField.isBlank()) {
                val messageRes = resources.getString(R.string.dialog_alert_message)
                val placeholder = if (isUsername) "User" else "Company"
                val message = String.format(messageRes, placeholder)

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                viewmodel.saveData(isUsername, inputField).also {
                    dialog.dismiss()
                }
            }
        }
    }
}