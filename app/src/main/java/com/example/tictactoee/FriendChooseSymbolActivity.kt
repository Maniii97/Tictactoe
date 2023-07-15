package com.example.tictactoee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class FriendChooseSymbolActivity : AppCompatActivity() {

    lateinit var btnCross : ImageView
    lateinit var btnCircle : ImageView
    lateinit var ivCross : ImageView
    lateinit var ivCircle : ImageView
    lateinit var btnBack : ImageView
    lateinit var btnContinue : Button
    var side = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_choose_symbol)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        btnCircle = findViewById(R.id.RdbtnCircleF)
        btnCross = findViewById(R.id.RdbtnCrossF)
        ivCircle= findViewById(R.id.ivCircleF)
        ivCross = findViewById(R.id.ivCrossF)
        btnBack = findViewById(R.id.btnBackF)
        btnContinue = findViewById(R.id.btnContinueF)

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        btnCircle.setOnClickListener{
            side = 1
            btnCircle.setImageResource(R.drawable.baseline_radio_button_checked_24)
            btnCross.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            ivCircle.alpha = 1f
            ivCross.alpha = 0.3f
        }

        btnCross.setOnClickListener{
            side = 0
            btnCross.setImageResource(R.drawable.baseline_radio_button_checked_24)
            btnCircle.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            ivCircle.alpha = 0.3f
            ivCross.alpha = 1f
        }

        ivCircle.setOnClickListener{
            side = 1
            btnCircle.setImageResource(R.drawable.baseline_radio_button_checked_24)
            btnCross.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            ivCircle.alpha = 1f
            ivCross.alpha = 0.3f
        }

        ivCross.setOnClickListener{
            side = 0
            btnCross.setImageResource(R.drawable.baseline_radio_button_checked_24)
            btnCircle.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            ivCircle.alpha = 0.3f
            ivCross.alpha = 1f
        }

        btnContinue.setOnClickListener {
            Intent(this,FriendsGameActivity::class.java).also{
                it.putExtra("side",side)
                startActivity(it)
            }
        }
    }
}