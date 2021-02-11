package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.databinding.ActivityProfileBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel
import com.ainul.oprek.ui.viewmodels.ProfileViewModel

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
    }
}