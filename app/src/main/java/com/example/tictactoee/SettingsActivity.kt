package com.example.tictactoee

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    lateinit var swVib : Switch
    lateinit var swSound : Switch
    lateinit var btnBack : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        swVib = findViewById(R.id.swVib)
        swSound = findViewById(R.id.swSound)
        btnBack = findViewById(R.id.btnBack)

        swVib.isChecked = Services.VIBRATION_CHECK
        swSound.isChecked = Services.SOUND_CHECK

        swVib.setOnCheckedChangeListener { _, isChecked ->
            Services.VIBRATION_CHECK = isChecked
        }
        swSound.setOnCheckedChangeListener { _, isChecked ->
            Services.SOUND_CHECK = isChecked
            Toast.makeText(this,"This feature is currently Unavailable",Toast.LENGTH_LONG).show()
        }
        btnBack.setOnClickListener {
            super.onBackPressed()
        }

    }

//    class SettingsFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }


}