package com.openconsent.demo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs: SharedPreferences = getSharedPreferences("openconsent", MODE_PRIVATE)
        prefs.edit().putBoolean("consent_given", false).apply()

        val consentGiven = prefs.getBoolean("consent_given", false)

        if (consentGiven) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, ConsentActivity::class.java))
        }
        finish()
    }
}