package com.example.epic

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.epic.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        }.addOnSuccessListener {
            showMessage("Database operation successfully")
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

    private fun changeActivity() {
        showMessage("Sign in successful")
        val intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)
    }


    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

}
