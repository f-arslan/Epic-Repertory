package com.example.epic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.epic.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseOperations: FirebaseOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseOperations = FirebaseOperations(this)

        signUpOperations()
    }

    private fun signUpOperations() {
        binding.signUpButton.setOnClickListener {
            val email = binding.signUpEtEmail.text.toString()
            val password = binding.signUpEtPassword.text.toString()
            val username = binding.signUpEtUsername.text.toString()
            val confirmPassword = binding.signUpEtRePassword.text.toString()
            val fullName = binding.signUpEtFullName.text.toString()
            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()) {
                showMessage("Please fill all the fields")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showMessage("Passwords do not match")
                return@setOnClickListener
            }
            if (!binding.signUpCbTerms.isChecked) {
                showMessage("Please accept the terms and conditions")
                return@setOnClickListener
            }

            val opStatusSignUp = firebaseOperations.signUpToAuthentication(email, password)
            signUpToDatabase(opStatusSignUp, username, fullName, email, password)

        }
    }

    private fun signUpToDatabase(
        opStatusSignUp: Boolean,
        username: String,
        fullName: String,
        email: String,
        password: String
    ) {
        if (!opStatusSignUp) {
            showMessage("Sign up to db failed")
            return
        }
        firebaseOperations.signUpToDatabase(fullName, email, username, password)
    }


    private fun showMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}