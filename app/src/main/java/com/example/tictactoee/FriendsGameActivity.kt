package com.example.tictactoee
// simple game activity with play with a friend on the 'same device'

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mikhaellopez.circularimageview.CircularImageView
import pl.droidsonroids.gif.GifImageView

class FriendsGameActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var box1 : ImageView
    private lateinit var box2 : ImageView
    private lateinit var box3 : ImageView
    private lateinit var box4 : ImageView
    private lateinit var box5 : ImageView
    private lateinit var box6 : ImageView
    private lateinit var box7 : ImageView
    private lateinit var box8 : ImageView
    private lateinit var box9 : ImageView

    private lateinit var vibrator : Vibrator
    private lateinit var gifSettings : GifImageView
    private lateinit var btnBack : ImageView
    private lateinit var civPlayer2 : CircularImageView
    private lateinit var civPlayer1 : CircularImageView

    var filledPos = intArrayOf(-1,-1,-1,-1,-1,-1,-1,-1,-1)

    var isGameActive = true

    var plWinCount = 0
    var botWinCount = 0

    var activePlayer = 0
    var pX = 0
    var pO = 1
    var sActivePlayer = 0

    var side = 0

    lateinit var tvBotWin : TextView
    lateinit var tvPlayerWin : TextView

    private lateinit var Boxes : Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_game)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        tvBotWin = findViewById(R.id.tvBotWin)
        tvPlayerWin = findViewById(R.id.tvPlayerWin)
        gifSettings = findViewById(R.id.gifSettings)
        btnBack = findViewById(R.id.btnBack)
        civPlayer1 = findViewById(R.id.civPlayer1)
        civPlayer2 = findViewById(R.id.civPlayer2)

        side = intent.getIntExtra("side",0)

        val drawable = gifSettings.drawable
        if(drawable is Animatable){
            drawable.stop()
        }

        box1 = findViewById(R.id.iv1)
        box2 = findViewById(R.id.iv2)
        box3 = findViewById(R.id.iv3)
        box4 = findViewById(R.id.iv4)
        box5 = findViewById(R.id.iv5)
        box6 = findViewById(R.id.iv6)
        box7 = findViewById(R.id.iv7)
        box8 = findViewById(R.id.iv8)
        box9 = findViewById(R.id.iv9)

        Boxes = arrayOf(box1,box2,box3,box4,box5,box6,box7,box8,box9)

        box1.setOnClickListener(this)
        box2.setOnClickListener(this)
        box3.setOnClickListener(this)
        box4.setOnClickListener(this)
        box5.setOnClickListener(this)
        box6.setOnClickListener(this)
        box7.setOnClickListener(this)
        box8.setOnClickListener(this)
        box9.setOnClickListener(this)


        if(side == 0){
            civPlayer1.borderWidth=10f
            civPlayer1.borderColorStart = Color.parseColor("#EB469A")
            civPlayer1.borderColorEnd = Color.parseColor("#7251DF")
            civPlayer1.borderColorDirection =CircularImageView.GradientDirection.TOP_TO_BOTTOM
            sActivePlayer = 0
            activePlayer = 0
        }

        if(side == 1){
            civPlayer2.borderWidth=10f
            civPlayer2.borderColorStart = Color.parseColor("#F7A27B")
            civPlayer2.borderColorEnd = Color.parseColor("#FF3D00")
            civPlayer2.borderColorDirection =CircularImageView.GradientDirection.TOP_TO_BOTTOM
            sActivePlayer = 1
            activePlayer = 1
        }

        gifSettings.setOnClickListener {
            val drawable = gifSettings.drawable
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

        btnBack.setOnClickListener {
            quitDialog()
        }

    }

    override fun onClick(view : View){
        val clickImg = view as ImageView
        val getTag = view.tag.toString().toInt()

        if(activePlayer == pX && filledPos[getTag - 1] == -1){
            if(Services.VIBRATION_CHECK){
                if(Build.VERSION.SDK_INT >= 26 ){
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else{
                    vibrator.vibrate(200)
                }
            }
            if(Services.SOUND_CHECK){
                // find a sound file and implement
            }
            clickImg.setImageResource(R.drawable.cross)
            sActivePlayer = pO
            activePlayer = sActivePlayer
            side = pO
            filledPos[(getTag - 1)]=pX

            checkForWin()
            if(isGameActive){
                checkDraw()
            }
        }
        else if(activePlayer == pO && filledPos[getTag -1] == -1){
            if(Services.VIBRATION_CHECK){
                if(Build.VERSION.SDK_INT >= 26 ){
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else{
                    vibrator.vibrate(200)
                }
            }
            if(Services.SOUND_CHECK){
                // find a sound file and implement
            }
            clickImg.setImageResource(R.drawable.circle)
            sActivePlayer = pX
            activePlayer = sActivePlayer
            side = pO
            filledPos[(getTag - 1)] = pO

            checkForWin()

            if(isGameActive){
                checkDraw()
            }

        }


    }

    private fun checkDraw() {
        var check = true
        for(i in 0..8){
            if(filledPos[i]==-1){
                check = false
            }
        }
        if(check){
            isGameActive = false
            Toast.makeText(this,"Game Draw", Toast.LENGTH_LONG).show()
            // dialog of drawgame
            drawDialog()
        }
    }

    private fun checkForWin() {
        val winningPos = arrayOf(
            intArrayOf(1, 2, 3),
            intArrayOf(4, 5, 6),
            intArrayOf(7, 8, 9),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(3, 6, 9),
            intArrayOf(1, 5, 9),
            intArrayOf(3, 5, 7)
        )
        for (i in 0..7) {
            val val0 = winningPos[i][0]
            val val1 = winningPos[i][1]
            val val2 = winningPos[i][2]

            if (filledPos[val0 - 1] == filledPos[val1 - 1] && filledPos[val1 - 1] == filledPos[val2 - 1]) {
                if (filledPos[val0 - 1] != -1) {
                    if (sActivePlayer == pX) {
                        if (side == 0) {
                            plWinCount++
                            tvPlayerWin.text = plWinCount.toString()
                            winDialog()
                        } else if (side == 1) {
                            botWinCount++
                            tvBotWin.text = botWinCount.toString()
                            lostDialog()
                        }
                        for (j in 0..2) {
                            Boxes[val0 - 1].setBackgroundResource(R.drawable.cross_background)
                            Boxes[val1 - 1].setBackgroundResource(R.drawable.cross_background)
                            Boxes[val2 - 1].setBackgroundResource(R.drawable.cross_background)
                        }
                        Toast.makeText(this, " X won ", Toast.LENGTH_LONG).show()
                    }
                    else if (sActivePlayer == pO) {
                        if (side == 0) {
                            botWinCount++
                            tvBotWin.text = botWinCount.toString()
                            lostDialog()
                        } else if (side == 1) {
                            plWinCount++
                            tvPlayerWin.text = plWinCount.toString()
                            winDialog()
                        }
                        for (j in 0..2) {
                            Boxes[val0 - 1].setBackgroundResource(R.drawable.circle_background)
                            Boxes[val1 - 1].setBackgroundResource(R.drawable.circle_background)
                            Boxes[val2 - 1].setBackgroundResource(R.drawable.circle_background)
                        }
                        Toast.makeText(this, " O won ", Toast.LENGTH_LONG).show()

                    }
                    isGameActive = false
                }

            }
        }
    }

    private fun restart() {
        for (i in 0..8) {
            filledPos[i] = -1
        }
        box1.setImageResource(0)
        box1.setBackgroundResource(0)
        box2.setImageResource(0)
        box2.setBackgroundResource(0)
        box3.setImageResource(0)
        box3.setBackgroundResource(0)
        box4.setImageResource(0)
        box4.setBackgroundResource(0)
        box5.setImageResource(0)
        box5.setBackgroundResource(0)
        box6.setImageResource(0)
        box6.setBackgroundResource(0)
        box7.setImageResource(0)
        box7.setBackgroundResource(0)
        box8.setImageResource(0)
        box8.setBackgroundResource(0)
        box9.setImageResource(0)
        box9.setBackgroundResource(0)
        isGameActive = true
    }

    private fun quitDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.quit_dialog)
        dialog.setCanceledOnTouchOutside(false)
        val quit = dialog.findViewById<Button>(R.id.btnQuit_q)
        val cancelBtn = dialog.findViewById<Button>(R.id.btnCancel_q)

        dialog.show()

        quit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,GameMenuActivity::class.java))
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun winDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.win_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        val btnExit = dialog.findViewById<Button>(R.id.btnQuit_w)
        val btnRematch = dialog.findViewById<Button>(R.id.btnContinue_w)

        dialog.show()

        btnExit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,GameMenuActivity::class.java))
        }
        btnRematch.setOnClickListener {
            dialog.dismiss()
            Handler().postDelayed({
                restart()
            }, 500)
        }
    }
    private fun drawDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.draw_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        val btnQuit = dialog.findViewById<Button>(R.id.btnQuit_draw)
        val btnContinue = dialog.findViewById<Button>(R.id.btnContinue_draw)

        dialog.show()

        btnQuit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,GameMenuActivity::class.java))
        }
        btnContinue.setOnClickListener {
            dialog.dismiss()
            Handler().postDelayed({
                restart()
            }, 500)
        }

    }


    private fun lostDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.lost_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        val quit = dialog.findViewById<Button>(R.id.btnQuit_l)
        val rematch = dialog.findViewById<Button>(R.id.btnContinue_l)

        dialog.show()

        quit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,GameMenuActivity::class.java))
        }
        rematch.setOnClickListener {
            dialog.dismiss()
            Handler().postDelayed({
                restart()
            }, 500)
        }
    }



}
