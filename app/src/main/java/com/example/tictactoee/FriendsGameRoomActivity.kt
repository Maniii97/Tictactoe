package com.example.tictactoee
// creates a room and lets 2 ppl on different devices play together

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FriendsGameRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_game_room)
    }
}