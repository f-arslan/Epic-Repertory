package com.example.epic

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epic.adapters.DetailCommentAdapter
import com.example.epic.data.DataSource
import com.example.epic.databinding.ActivityDetailBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DetailActivity : AppCompatActivity(), DetailCommentAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDetailBinding
    private var musicDataSource = DataSource().loadMusics()
    private var commentDataSource = DataSource().loadComments()
    private var adapter = DetailCommentAdapter(commentDataSource, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent
        val intent = intent
        val title: String? = intent.getStringExtra("MUSIC")

        fillDataFromDb(title)

        // TextView scrollable
        tvScrollable()
        toneAdapterSettings()
        recycleViewInit()

        sendCommentButton()


    }

    private fun sendCommentButton() {
        binding.detailBtnCommentSend.setOnClickListener {
            val comment = binding.detailCommentEt.text.toString()
            if (comment.isEmpty()) {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }
    }

    private fun tvScrollable() {
        binding.detailTvLyrics.movementMethod = ScrollingMovementMethod()
    }

    private fun recycleViewInit() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.apply {
            detailCommentRecyclerView.layoutManager = linearLayoutManager
            detailCommentRecyclerView.adapter = adapter
        }
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

    override fun onItemClick(position: Int) {
        return
    }
}