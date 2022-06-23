package com.example.epic.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.epic.FirebaseOperations
import com.example.epic.R
import com.example.epic.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SettingsFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var firebaseOperations: FirebaseOperations
    private lateinit var binding: FragmentSettingsBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.bind(viewOfLayout)

        firebaseOperations = FirebaseOperations(this.requireContext())


        val username = firebaseOperations.readUserNameFromFile()
        binding.settingsWelcomeTextView.text = "Welcome, $username"


        firebaseDatabase.getReference("Users").child(username!!).child("email").get()
            .addOnSuccessListener {
                val emailSpan = "<b>Email: </b>${it.value}<br/>"
                binding.settingsEmailTextView.text = Html.fromHtml(emailSpan)
            }

        // UpdateTheDatabase
        updateTheInformationOnDatabase(username)

        return viewOfLayout
    }

    private fun updateTheInformationOnDatabase(username: String?) {
        binding.settingsUpdateButton.setOnClickListener {
            val name = binding.settingsNameEditText.text.toString()
            val password = binding.settingsPasswordEditText.text.toString()
            val rePassword = binding.settingsRePasswordEditText.text.toString()

            if (name.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (password != rePassword) {
                Toast.makeText(this.requireContext(), "Passwords do not match", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle("Update")
            builder.setMessage("Are you sure you want to update your information?")
            builder.setPositiveButton("Yes") { _, _ ->
                firebaseAuth.currentUser?.updatePassword(password)
                firebaseDatabase.getReference("Users").child(username!!).child("fullName")
                    .setValue(name)
                firebaseDatabase.getReference("Users").child(username).child("password")
                    .setValue(password)
                Toast.makeText(this.requireContext(), "Information updated", Toast.LENGTH_LONG)
                    .show()
            }
            builder.setNegativeButton("No") { _, _ ->
                Toast.makeText(this.requireContext(), "Information not updated", Toast.LENGTH_LONG)
                    .show()
            }
            builder.show()
        }

    }
}