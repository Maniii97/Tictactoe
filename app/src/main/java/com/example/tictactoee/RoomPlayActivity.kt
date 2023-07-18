package com.example.tictactoee

import android.app.GameState
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import pl.droidsonroids.gif.GifImageView

class RoomPlayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var box1: ImageView
    private lateinit var box2: ImageView
    private lateinit var box3: ImageView
    private lateinit var box4: ImageView
    private lateinit var box5: ImageView
    private lateinit var box6: ImageView
    private lateinit var box7: ImageView
    private lateinit var box8: ImageView
    private lateinit var box9: ImageView

    private lateinit var vibrator: Vibrator
    private lateinit var gifSettings: GifImageView
    private lateinit var btnBack: ImageView
    private lateinit var civPlayer2: CircularImageView
    private lateinit var civPlayer1: CircularImageView

    private lateinit var gameCode: String
    private lateinit var gameId: String
    private lateinit var activePlayer: String
    private lateinit var gameRef: DatabaseReference
    private lateinit var gameListener: ValueEventListener

    private lateinit var tvBotWin: TextView
    private lateinit var tvPlayerWin: TextView

    private var filledPos = IntArray(9) { -1 }

    private var isGameActive = false
    private var side: Int = 0
    private var plWinCount: Int = 0
    private var botWinCount: Int = 0
    private val pX: Int = 0
    private val pO: Int = 1
    private val sActivePlayer: Int = 0


    private lateinit var Boxes: Array<ImageView>

    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_play)

        val roomCode = intent.getStringExtra("roomCode")

        supportActionBar?.hide()

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        tvBotWin = findViewById(R.id.tvBotWin)
        tvPlayerWin = findViewById(R.id.tvPlayerWin)
        gifSettings = findViewById(R.id.gifSettings)
        btnBack = findViewById(R.id.btnBack)
        civPlayer1 = findViewById(R.id.civPlayer1)
        civPlayer2 = findViewById(R.id.civPlayer2)

        box1 = findViewById(R.id.iv1)
        box2 = findViewById(R.id.iv2)
        box3 = findViewById(R.id.iv3)
        box4 = findViewById(R.id.iv4)
        box5 = findViewById(R.id.iv5)
        box6 = findViewById(R.id.iv6)
        box7 = findViewById(R.id.iv7)
        box8 = findViewById(R.id.iv8)
        box9 = findViewById(R.id.iv9)

        Boxes = arrayOf(box1, box2, box3, box4, box5, box6, box7, box8, box9)

        box1.setOnClickListener(this)
        box2.setOnClickListener(this)
        box3.setOnClickListener(this)
        box4.setOnClickListener(this)
        box5.setOnClickListener(this)
        box6.setOnClickListener(this)
        box7.setOnClickListener(this)
        box8.setOnClickListener(this)
        box9.setOnClickListener(this)

        gifSettings.setOnClickListener {
            val drawable = gifSettings.drawable
            if (drawable is Animatable) {
                drawable.start()
            }
            Handler().postDelayed({
                if (drawable is Animatable) {
                    drawable.stop()
                }
                startActivity(Intent(this, SettingsActivity::class.java))
            }, 750)
        }

        btnBack.setOnClickListener {
            quitDialog()
        }
        createGame()
    }


    override fun onClick(view: View) {

        val clickImg = view as ImageView
        val getTag = view.tag.toString().toInt()

        if (isGameActive && activePlayer == "X" && filledPos[getTag - 1] == -1) {
            if (Services.VIBRATION_CHECK) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            200,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(200)
                }
            }
            if (Services.SOUND_CHECK) {
                // play sound
            }
            clickImg.setImageResource(R.drawable.cross)
            filledPos[getTag - 1] = pX
            activePlayer = if (gameCode == "X") "O" else "X"
            gameRef.child("filledPositions").setValue(filledPos.toList())
            gameRef.child("activePlayer").setValue(activePlayer)


        } else if (isGameActive && activePlayer == "O" && filledPos[getTag - 1] == -1) {
            if (Services.VIBRATION_CHECK) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            200,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(200)
                }
            }
            if (Services.SOUND_CHECK) {
                // play sound
            }
            clickImg.setImageResource(R.drawable.circle)
            filledPos[getTag - 1] = pX
            activePlayer = if (gameCode == "O") "X" else "O"
            gameRef.child("filledPositions").setValue(filledPos.toList())
            gameRef.child("activePlayer").setValue(activePlayer)

        }
    }

    private fun createGame() {
        gameRef = databaseReference.child("games").push()
        gameId = gameRef.key!!
        gameCode = if (side == 0) "X" else "O"
        activePlayer = "X"

        gameRef.setValue(GameState(gameCode, activePlayer, filledPos.toList()))

        listenForGameUpdates()
    }

    private fun listenForGameUpdates() {
        gameListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gameState = dataSnapshot.getValue(GameState::class.java)
                if (gameState != null) {
                    gameCode = gameState.gameCode
                    activePlayer = gameState.activePlayer
                    filledPos = gameState.filledPositions.toIntArray()

                    if (activePlayer == gameCode) {
                        isGameActive = true
                    } else {
                        isGameActive = false
                        Toast.makeText(this@RoomPlayActivity, "Waiting for opponent's move", Toast.LENGTH_SHORT).show()
                    }


                    updateGameBoard()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        gameRef.addValueEventListener(gameListener)
    }

    private fun quitDialog() {
        TODO("Not yet implemented")
    }

    data class GameState(
        val gameCode: String = "",
        val activePlayer: String = "",
        val filledPositions: List<Int> = listOf()
    )

    private fun updateGameBoard() {
        for (i in filledPos.indices) {
            if (filledPos[i] == pX) {
                Boxes[i].setImageResource(R.drawable.cross)
            } else if (filledPos[i] == pO) {
                Boxes[i].setImageResource(R.drawable.circle)
            }
        }


        gameRef.child("filledPositions").setValue(filledPos.toList())
        gameRef.child("activePlayer").setValue(activePlayer)

        checkForWin()
        checkDraw()
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
                            plWinCount+=1
                            tvPlayerWin.text = plWinCount.toString()
                            //winDialog()
                        }
                        else if (side == 1) {
                            botWinCount+=1
                            tvBotWin.text = botWinCount.toString()
                            //robotWinDialog()
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
                            botWinCount+=1
                            tvBotWin.text = botWinCount.toString()
                            //robotWinDialog()
                        }
                        else if (side == 1) {
                            plWinCount+=1
                            tvPlayerWin.text = plWinCount.toString()
                            //winDialog()
                        }
                        for (j in 0..2) {
                            Boxes[val0 - 1].setBackgroundResource(R.drawable.circle_background)
                            Boxes[val1 - 1].setBackgroundResource(R.drawable.circle_background)
                            Boxes[val2 - 1].setBackgroundResource(R.drawable.circle_background)
                        }
                        // make a dialog for win and then display it here
                        Toast.makeText(this, " O won ", Toast.LENGTH_LONG).show()

                    }
                    isGameActive = false
                }

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
            Toast.makeText(this,"Game Draw",Toast.LENGTH_LONG).show()
            //drawDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameRef.removeEventListener(gameListener)
    }
}
