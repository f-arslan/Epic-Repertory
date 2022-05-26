package com.example.epic

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.epic.data.Comment
import com.example.epic.data.Music
import com.example.epic.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.InputStreamReader

class FirebaseOperations(private val context: Context) {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()


    private fun getUser(email: String) {
        firebaseDatabase.getReference("Users").get().addOnSuccessListener {
            if (it.exists()) {
                for (user in it.children) {
                    val userData = user.getValue(User::class.java)
                    if (userData!!.email == email) {
                        // Create txt file and add the username
                        val file = context.openFileOutput("user.txt", Context.MODE_PRIVATE)
                        file.write(userData.username?.toByteArray())
                    }
                }
            }
        }
    }

    fun readUserNameFromFile(): String {
        val fileInputStream = context.openFileInput("user.txt")
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        val stringBuilder: StringBuilder = StringBuilder()
        var text: String?

        while (run {
                text = bufferedReader.readLine()
                text
            } != null) {
            stringBuilder.append(text)
        }
        bufferedReader.close()
        return stringBuilder.toString()
    }

    fun signUpToAuthentication(email: String, password: String): Boolean {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                showMessage("Account creation failed")
                return@addOnCompleteListener
            }

            Thread.sleep(150)
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(context, intent, null)
        }
        return true
    }

    fun signUpToDatabase(
        fullName: String,
        email: String,
        username: String,
        password: String
    ) {
        val user = User(username, fullName, email, password, ",")
        val database = firebaseDatabase.getReference("Users")
        database.child(username).setValue(user).addOnFailureListener {
            showMessage("Database operation failed")
            return@addOnFailureListener
        }
    }

    fun signInAuthentication(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                showMessage("Sign in failed")
                return@addOnCompleteListener
            }
            getUser(email)
            changeActivity()
        }

    }

    fun addCommentToDatabase(comment: Comment, musicId: String) {
        val database = firebaseDatabase.getReference("Comments")
        database.child(musicId).push().setValue(comment).addOnFailureListener {
            showMessage(it.toString())
            return@addOnFailureListener
        }
    }

    fun addMusicToDatabase(music: Music, username: String) {
        val database = firebaseDatabase.getReference("Musics")
        val userDatabase = firebaseDatabase.getReference("Users")
        database.child(music.id.toString()).setValue(music).addOnFailureListener {
            showMessage("Database operation failed")
            return@addOnFailureListener
        }
        userDatabase.child(username).child("musicList").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showMessage("Database operation failed")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val musicList = p0.value.toString()
                val newMusicList = musicList + "," + music.id.toString()
                userDatabase.child(username).child("musicList").setValue(newMusicList)
                Log.i("MusicList", newMusicList)
            }
        })

    }

    private fun changeActivity() {
        showMessage("Sign in successful")
        val intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)
    }


    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

}
