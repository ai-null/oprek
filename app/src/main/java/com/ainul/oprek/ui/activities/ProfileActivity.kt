package com.ainul.oprek.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ainul.oprek.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
    }
}