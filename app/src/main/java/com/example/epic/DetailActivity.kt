package com.example.epic

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var globalChordList: List<String>
    private lateinit var globalLyricsList: List<String>
    private lateinit var currentTone: String
    private var commentAdapter: DetailCommentAdapter =
        DetailCommentAdapter(commentInitializer, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent
        val intent = intent
        val musicId: String? = intent.getStringExtra("MUSIC_ID")

        fillDataFromDb(musicId)

        tvScrollable()
        loadCommentFromDb(musicId)
        recycleViewInit()
        sendCommentButton(musicId)
        tonesListOperations()
    }

    private fun tonesListOperations() {
        val tones: List<String> =
            listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        binding.detailToneTextView.setOnItemClickListener { adapterView, _, i, _ ->
            val selectedTone: String = adapterView.getItemAtPosition(i).toString()
            val selectedToneIndex: Int = tones.indexOf(selectedTone)
            val totalMove: Int = selectedToneIndex - tones.indexOf(currentTone)
            val tempChordList: MutableList<String> = mutableListOf()
            if (totalMove == 0) {
                preprocessLyrics(globalLyricsList.joinToString("|"), globalChordList.joinToString("|"))
            } else {
                for (line in globalChordList) {
                    // Update the chord
                    tempChordList.add(updateChord(line, totalMove))
                }
                preprocessLyrics(globalLyricsList.joinToString("|"), tempChordList.joinToString("|"))
            }
        }
    }

    private fun updateChord(line: String, totalMove: Int): String {
        val tones: List<String> =
            listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val realList = line.split(" ")
        val tempList: MutableList<String> = mutableListOf()
        for (i in realList.indices) {
            if (realList[i].lowercase().contains("m")) {
                val otherLetter = realList[i].substring(0, realList[i].length - 1)
                val newValueIndex = (tones.indexOf(otherLetter) + totalMove) % tones.size
                tempList.add(tones[newValueIndex] + "m")
            } else if (realList[i].isNotEmpty()) {
                val newValueIndex = (tones.indexOf(realList[i]) + totalMove) % tones.size
                tempList.add(tones[newValueIndex])
            } else {
                tempList.add("")
            }
        }
        return tempList.joinToString(" ")

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
        if (musicId == null) return
        firebaseDatabase.getReference("Musics").addValueEventListener(object :
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
                        if (lyrics != null) {
                            globalLyricsList = lyrics.split("|")
                        }
                        if (chords != null) {
                            globalChordList = chords.split("|")
                        }
                        preprocessLyrics(lyrics, chords)
                        music?.cover?.let { getImageFromStorage(it) }
                        val tone = music?.tones
                        if (tone != null) {
                            currentTone = tone
                            toneAdapterSettings(tone)
                        }

                        return
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun preprocessLyrics(lyrics: String?, chords: String?) {
        val spannableStringBuilder = SpannableStringBuilder()
        val lyricsList = lyrics?.split("|")
        val chordsList = chords?.split("|")

        if (lyricsList != null) {
            for (i in lyricsList.indices) {
                spannableStringBuilder.append(lyricsList.get(i))
                spannableStringBuilder.append("\n")
                val red = ForegroundColorSpan(Color.rgb(150, 0, 0))
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
        }
        // Remove last new line
        spannableStringBuilder.delete(
            spannableStringBuilder.length - 1,
            spannableStringBuilder.length
        )
        binding.detailTvLyrics.text = spannableStringBuilder

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