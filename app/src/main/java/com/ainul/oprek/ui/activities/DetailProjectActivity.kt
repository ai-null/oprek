package com.ainul.oprek.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ainul.oprek.R

class DetailProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_project)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}