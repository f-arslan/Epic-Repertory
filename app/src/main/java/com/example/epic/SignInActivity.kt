package com.example.epic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.epic.databinding.ActivitySignInBinding
import com.example.epicrepertory.FirebaseOperations

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // For debug
        binding.apply {
            signInEtEmail.setText("f@gmail.com")
            signInEtPassword.setText("123456")
        }

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
}