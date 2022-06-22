package com.example.epic

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.epic.data.Comment
import com.example.epic.data.Music
import com.example.epic.data.User
import com.example.epic.fragments.AddMusicsFragment
import com.example.epic.fragments.HomeFragment
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


    fun getUser(email: String) {
        firebaseDatabase.getReference("Users").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseOperations", "Error: $error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    if (user.child("email").value == email) {
                        val userData = user.getValue(User::class.java)
                        Log.i("FirebaseOperations USERNAME", "User: $userData")
                        if (userData != null) {
                            // Write username to "user.txt"
                           writeToFile(userData.username)
                        }
                        break
                    }
                }
            }
        })

    }

    private fun writeToFile(username: String?) {
        val file = context.openFileOutput("user.txt", Context.MODE_PRIVATE)
        file.write(username?.toByteArray())
        file.close()
    }

    fun readUserNameFromFile(): String? {
        val file = context.openFileInput("user.txt")
        val reader = BufferedReader(InputStreamReader(file))
        val username = reader.readLine()
        reader.close()
        return username
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
        val user = User(username, fullName, email, password, "")
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
            if (task.isSuccessful) {
                getUser(email)
            }
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
                Log.i("musicList", musicList)
                val newMusicList: String = if (musicList == "null") {
                    music.id.toString()
                } else {
                    musicList + "," + music.id.toString()
                }
                userDatabase.child(username).child("musicList").setValue(newMusicList)
                Log.i("MusicList", newMusicList)
            }
        })

    }

    private fun changeActivity() {
        showMessage("Sign in successful")
        Log.i("FirebaseOperations", "Sign in successful")
        val intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)
    }


    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

}
