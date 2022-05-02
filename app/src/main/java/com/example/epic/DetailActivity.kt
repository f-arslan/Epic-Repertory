package com.example.epic

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.epic.data.DataSource
import com.example.epic.databinding.ActivityDetailBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var musicDataSource = DataSource().loadMusics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent
        val intent = intent
        val title: String? = intent.getStringExtra("MUSIC")

        fillDataFromDb(title)

        toneAdapterSettings()


    }

    private fun fillDataFromDb(title: String?) {
        val music = musicDataSource.find { it.title == title }
        if (music != null) {
            binding.apply {
                detailTvTitle.text = music.title
                detailTvLyrics.text = music.lyrics
                music.cover?.let {getImageFromStorage(it)}
            }
        }
    }

    private fun getImageFromStorage(imageName: String) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("images/$imageName.png")
        val localFile = File.createTempFile(imageName, "png")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.detailIwImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toneAdapterSettings() {
        val tones = resources.getStringArray(R.array.tones)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, tones)
        binding.detailAutoCompleteTextView.setAdapter(adapter)
    }
}