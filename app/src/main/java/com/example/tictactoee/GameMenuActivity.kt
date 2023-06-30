package com.example.tictactoee

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import pl.droidsonroids.gif.GifImageView

class GameMenuActivity : AppCompatActivity(), OnTouchListener {
    private var SCREEN_SIZE = 0
    private var SET_TRANSLATE = 0
    private var animationStarted = false
    private lateinit var settingsGifView: GifImageView
    private lateinit var btnWithFriends: Button
    private lateinit var btnWithBot:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        SCREEN_SIZE = getScreenResolution(this)
        SET_TRANSLATE = if (SCREEN_SIZE > 1500) {
            -560
        } else {
            -300
        }

        settingsGifView = findViewById<View>(R.id.setting_gifview_menu) as GifImageView
        btnWithFriends = findViewById(R.id.btnWithFriends)
        btnWithBot = findViewById(R.id.btnWithBot)

        val drawable = settingsGifView.drawable
        if(drawable is Animatable){
            drawable.stop()
        }

        settingsGifView.setOnClickListener {
            val drawable = settingsGifView.drawable
            if (drawable is Animatable){
                drawable.start()
            }
            Handler().postDelayed({
                if (drawable is Animatable){
                    drawable.stop()
                }
                Intent(this,SettingsActivity::class.java).also {
                    startActivity(it)

                }
            },750)
        }

        btnWithFriends.setOnClickListener{
            Intent(this,FriendsGameMenuActivity::class.java).also{
                startActivity(it)

            }
        }
        btnWithBot.setOnClickListener {
            Intent(this,BotGameActivity::class.java).also{
                startActivity(it)
            }
        }
    }

    private fun getScreenResolution(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if(animationStarted || !hasFocus){
            return
        }
        animate()
        super.onWindowFocusChanged(hasFocus)

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}


    private fun animate() {
        val logo = findViewById<ImageView>(R.id.img_logo_offline_menu)
        val container = findViewById<ViewGroup>(R.id.ContainerGameMenu)

        ViewCompat.animate(logo)
            .translationY(SET_TRANSLATE.toFloat())
            .setStartDelay(STARTUP_DELAY.toLong())
            .setDuration(ANIM_ITEM_DURATION.toLong())
            .setInterpolator(DecelerateInterpolator(1.2f))
            .start()

        for(i in 0 until container.childCount){
            val gc = container.getChildAt(i)
            val viewAnimator : ViewPropertyAnimatorCompat = if (gc !is Button) {
                ViewCompat.animate(gc)
                    .setDuration(1000)
                    .translationY(50f)
                    .alpha(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
            }else {
                ViewCompat.animate(gc)
                    .scaleY(1f)
                    .scaleX(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
                    .setDuration(500)
            }
            viewAnimator.setInterpolator(DecelerateInterpolator()).start()

        }

    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if(p0=== btnWithFriends || p0 === btnWithBot){
            if (p1 != null) {
                when(p1.action){
                    MotionEvent.ACTION_DOWN -> p0.alpha = 0.5f
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> p0.alpha = 1f
                }
            }
        }
        return false
    }

    companion object {
        const val STARTUP_DELAY = 300
        const val ANIM_ITEM_DURATION = 1000
        const val ITEM_DELAY = 300
    }

}
