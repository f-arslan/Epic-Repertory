package com.example.epic

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.epic.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginProcess()


    }


    private fun loginProcess() {
        val firebaseOperations = FirebaseOperations(this)
        binding.btnLogin.setOnClickListener {
            val email = binding.signInEtEmail.text.toString()
            val password = binding.signInEtPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            firebaseOperations.signInAuthentication(email, password)
        }
    }

    fun goToSignUpPage(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}