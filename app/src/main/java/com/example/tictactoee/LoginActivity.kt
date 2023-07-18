package com.example.tictactoee
// login using Google, using firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var btnSignin: Button
    lateinit var etUsername : EditText

    lateinit var databaseReference: DatabaseReference

    var uidSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseReference = FirebaseDatabase.getInstance().reference

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        btnSignin = findViewById(R.id.btnSignin)
        etUsername = findViewById(R.id.etUsername)
        auth = FirebaseAuth.getInstance()

//        val currentUser: FirebaseUser? = auth.currentUser
//        if (currentUser != null) {
//            redirectToGameMenuActivity()
//        }


        btnSignin.setOnClickListener {
            savingUsername()
            if (uidSaved) {
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webclient_id))
                    .requestEmail()
                    .build()
                val signinClient = GoogleSignIn.getClient(this, options)
                signinClient.signInIntent.also {
                    startActivityForResult(it, 0)
                }
            }
            else{
                Toast.makeText(this,"Username not saved, try again ",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun redirectToGameMenuActivity() {
        startActivity(Intent(this,GameMenuActivity::class.java))
        finish()
    }

    fun savingUsername(){
        val username = etUsername.text.toString().trim()
        if(username.isEmpty()){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
        }
        val currentUser : FirebaseUser? = auth.currentUser
        if(currentUser != null){
            val userId = currentUser.uid
            uidSaved = true
        }

        databaseReference.child("usernames").child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Toast.makeText(this@LoginActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseReference.child("usernames").child(username).setValue(true)
                    Toast.makeText(this@LoginActivity, "Username saved", Toast.LENGTH_SHORT).show()


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }

        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginActivity," logged in successfully", Toast.LENGTH_LONG).show()
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(1000L)
                        startActivity(Intent(this@LoginActivity,GameMenuActivity::class.java))
                    }

                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}