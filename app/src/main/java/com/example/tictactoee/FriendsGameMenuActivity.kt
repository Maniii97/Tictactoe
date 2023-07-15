package com.example.tictactoee

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.Toast
import pl.droidsonroids.gif.GifImageView

class FriendsGameMenuActivity : AppCompatActivity() {

    lateinit var btnRoom : Button
    lateinit var btnThisdevice : Button
    lateinit var gifSetting : GifImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_game_menu)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        btnRoom = findViewById(R.id.btnRoom)
        btnThisdevice = findViewById(R.id.btnThisdevice)
        gifSetting = findViewById(R.id.gifSetting)

        val drawable = gifSetting.drawable
        if(drawable is Animatable){
            drawable.stop()
        }
        gifSetting.setOnClickListener {
            val drawable = gifSetting.drawable
            if (drawable is Animatable){
                drawable.start()
            }
            Handler().postDelayed({
                if(drawable is Animatable){
                    drawable.stop()
                }
                Intent(this,SettingsActivity::class.java).also{
                    startActivity(it)
                }

            },750)
        }

        btnRoom.setOnClickListener {

//            Toast.makeText(this,"This feature will be available soon", Toast.LENGTH_LONG).show()
           Intent(this,FriendsGameRoomActivity::class.java).also {
                startActivity(it)
           }
        }
        btnThisdevice.setOnClickListener {
            Intent(this, FriendChooseSymbolActivity::class.java).also{
                startActivity(it)
            }
        }
    }

}