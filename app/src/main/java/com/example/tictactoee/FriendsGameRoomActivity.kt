package com.example.tictactoee
// creates a room and lets 2 ppl on different devices play together

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tictactoee.R.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class FriendsGameRoomActivity : AppCompatActivity() {

    private var roomCode : String = ""
    var isHost : Boolean = false
    var isOpponentReady: Boolean = false

    val user = Firebase.auth.currentUser

    lateinit var btnCreate : Button
    lateinit var btnJoin : Button

    lateinit var OppID : String
    lateinit var HostID : String


    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tictactoee-b12f6-default-rtdb.firebaseio.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_friends_game_room)
        supportActionBar?.hide()

        btnCreate = findViewById(id.createBtn)
        btnJoin = findViewById(id.joinBtn)

        btnCreate.setOnClickListener {
            generateRoomCode()
            dialogforCreate()
        }

        btnJoin.setOnClickListener {
            dialogforJoin()
        }

//        playerUid = System.currentTimeMillis().toString() // generates players uid,
    }
    fun generateRoomCode() {
        roomCode = generateRandomCode()
        isHost = true

        databaseReference.child("rooms").child(roomCode).setValue(isHost)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listenForOpponentReady()
                } else {
                    // Handle error if necessary
                }
            }
    }

    private fun listenForOpponentReady() {

        databaseReference.child("rooms").child(roomCode).child("isOpponentReady")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readyStatus = snapshot.getValue(Boolean::class.java)
                    if ((readyStatus != null) && readyStatus) {
                        isOpponentReady = true
                        startGame(roomCode, playerId = OppID ,false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun startGame(roomCode: String,playerId : String, isHost : Boolean) {
        Intent(this,RoomPlayActivity::class.java).also {
            startActivity(it)
            it.putExtra("roomCode",roomCode)
            it.putExtra("playerId",playerId)
            it.putExtra("isHost",isHost)
        }
    }


    private fun generateRandomCode(): String {

        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")

    }

    fun dialogforCreate(){
        val dialog = Dialog(this)
        dialog.setContentView(layout.create_room_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        val host = true
        HostID = user?.let {
            val uid = it.uid
        }.toString()

        dialog.show()

        val start = dialog.findViewById<Button>(id.start)
        val tvRoomCode = dialog.findViewById<TextView>(id.tvRoomCode)
        tvRoomCode.text = roomCode
        start.setOnClickListener {
            if(isOpponentReady){
                // start game activity
                dialog.dismiss()
                startGame(roomCode,HostID,true)
            }
            else{
                Toast.makeText(this,"Wait for the Opponent to join",Toast.LENGTH_LONG).show()
            }
        }




    }

    fun dialogforJoin(){
        val dialog = Dialog(this)
        dialog.setContentView(layout.join_room_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        val etRoomCode = dialog.findViewById<EditText>(id.etRoomCode)
        val join = dialog.findViewById<Button>(id.join)

        val code = etRoomCode.text.toString()
        val host = false

        OppID = user?.let {
            val uid = it.uid
        }.toString()


        dialog.show()

        join.setOnClickListener {
            val code = etRoomCode.text.toString()
            if (code.isNotEmpty()) {
                databaseReference.child("rooms").child(code).get().addOnSuccessListener { dataSnapshot ->
                    val isHost = dataSnapshot.getValue(Boolean::class.java)
                    if (isHost != null && isHost) {
                        this.isHost = false
                        isOpponentReady = true
                        dialog.dismiss()
                        databaseReference.child("rooms").child(code).child("isOpponentReady").setValue(true)
                        startGame(code,OppID,host)
                    } else {
                        Toast.makeText(this, "Entered Room Key is Incorrect", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error retrieving room information", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid Room Key", Toast.LENGTH_LONG).show()
            }
        }
    }


}