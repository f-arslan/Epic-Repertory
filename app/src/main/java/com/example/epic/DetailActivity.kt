package com.example.epic

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epic.adapters.DetailCommentAdapter
import com.example.epic.data.Comment
import com.example.epic.data.DataSource
import com.example.epic.data.Music
import com.example.epic.databinding.ActivityDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), DetailCommentAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDetailBinding
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var toneAdapter: ArrayAdapter<String>
    private var commentInitializer = DataSource().loadComments()
    private var firebaseOperations = FirebaseOperations(this)
    private var commentAdapter: DetailCommentAdapter = DetailCommentAdapter(commentInitializer, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent
        val intent = intent
        val musicId: String? = intent.getStringExtra("MUSIC_ID")

        fillDataFromDb(musicId)

        // TextView scrollable
        tvScrollable()
        loadCommentFromDb(musicId)
        recycleViewInit()
        sendCommentButton(musicId)


    }

    @SuppressLint("SimpleDateFormat")
    private fun sendCommentButton(musicId: String?) {
        binding.detailBtnCommentSend.setOnClickListener {
            val comment = binding.detailCommentEt.text.toString()
            if (comment.isEmpty()) {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val username = firebaseOperations.readUserNameFromFile()
            // Get data as day-Month-year
            val date = SimpleDateFormat("dd-MM-yyyy").format(Date()).toString()
            val commentObject = Comment(
                musicId,
                username,
                comment,
                date
            )
            if (musicId != null) {
                firebaseOperations.addCommentToDatabase(commentObject, musicId).also {
                    binding.detailCommentEt.text?.clear()
                }
            }

        }
    }

    private fun loadCommentFromDb(musicId: String?) {
        if (musicId == null) {
            return
        }
        firebaseDatabase.getReference("Comments").child(musicId).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val comments = mutableListOf<Comment>()
                    for (comment in snapshot.children) {
                        val commentObject = comment.getValue(Comment::class.java)
                        comments.add(commentObject!!)
                    }
                    commentAdapter = DetailCommentAdapter(comments, this@DetailActivity)
                    commentAdapter.notifyDataSetChanged()
                    binding.detailCommentRecyclerView.adapter = commentAdapter
                    return
                }
            }
        })
    }

    private fun tvScrollable() {
        binding.detailTvLyrics.movementMethod = ScrollingMovementMethod()
    }

    private fun recycleViewInit() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.apply {
            detailCommentRecyclerView.layoutManager = linearLayoutManager
            detailCommentRecyclerView.adapter = commentAdapter
        }
    }

    private fun fillDataFromDb(musicId: String?) {
        if (musicId != null) {
            firebaseDatabase.getReference("Musics").addValueEventListener(object:
            ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        if (data.key != musicId) {
                            continue
                        }
                        val music = data.getValue(Music::class.java)
                        binding.apply {
                            detailTvTitle.text = music?.title
                            val lyrics = music?.lyrics
                            val chords = music?.chords
                            preprocessLyrics(lyrics, chords)
                            music?.cover?.let { getImageFromStorage(it) }
                            val tone = music?.tones
                            if (tone != null) {
                                toneAdapterSettings(tone)
                            }

                            return
                        }
                    }
                }

                private fun preprocessLyrics(lyrics: String?, chords: String?) {
                    val spannableStringBuilder = SpannableStringBuilder()
                    val lyricsList = lyrics?.split("|")
                    val chordsList = chords?.split("|")

                    for (i in 0 until lyricsList?.size!!) {
                        spannableStringBuilder.append(lyricsList[i])
                        spannableStringBuilder.append("\n")
                        val red = ForegroundColorSpan(Color.RED)
                        val spannableStringBuilderChord = SpannableStringBuilder(chordsList?.get(i))
                        chordsList?.get(i)
                            .let {
                                if (it != null) {
                                    spannableStringBuilderChord.setSpan(red, 0, it.length, 0)
                                }
                            }
                        spannableStringBuilder.append(spannableStringBuilderChord)
                        spannableStringBuilder.append("\n")
                    }
                    // Remove last new line
                    spannableStringBuilder.delete(spannableStringBuilder.length - 1, spannableStringBuilder.length)
                    binding.detailTvLyrics.text = spannableStringBuilder

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

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

    private fun toneAdapterSettings(tone: String) {
        // Change place of tone with the first index
        binding.detailToneTextView.setText(tone)
        val tones = resources.getStringArray(R.array.tones)
        toneAdapter = ArrayAdapter(this, R.layout.dropdown_item, tones)
        binding.detailToneTextView.setAdapter(toneAdapter)

    }

    override fun onItemClick(position: Int) {
        return
    }
}