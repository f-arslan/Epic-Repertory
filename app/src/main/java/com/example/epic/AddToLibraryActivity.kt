package com.example.epic

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.epic.data.Music
import com.example.epic.databinding.ActivityAddToLibraryBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AddToLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToLibraryBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var firebaseOperations: FirebaseOperations
    private var lyricsList: MutableList<String>? = mutableListOf()
    private var chordsList: MutableList<String>? = mutableListOf()
    private var rhythmList: MutableList<String>? = mutableListOf()
    private var musicObject: Music? = null
    var executed = false

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicId: String? = intent.getStringExtra("MUSIC_ID")
        firebaseOperations = FirebaseOperations(context = this)

        GlobalScope.launch(Dispatchers.IO) {
            // First, wait until the music is loaded in loadMusicToDatabase function to get the lyrics
            // After, run the editTextOperations function
            if (!executed) {
                loadMusicsToEditTexts(musicId)
                executed = true
            }
            GlobalScope.launch(Dispatchers.Main) {
                editTextOperations()
                saveToDatabase()
            }
        }
    }

    private fun saveToDatabase() {
        binding.librarySaveButton.setOnClickListener {
            val lyrics = lyricsList?.joinToString("|")
            val chords = chordsList?.joinToString("|")
            val rhythm = rhythmList?.joinToString("|")
            musicObject?.lyrics = lyrics
            musicObject?.chords = chords
            musicObject?.rhythms = rhythm
            musicObject?.id = createIdForMusic()
            val title = binding.libraryTitleEditText.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            musicObject?.title = title
            musicObject?.isGlobalMusic = "false"
            val username = firebaseOperations.readUserNameFromFile()
            if (username != null) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Save to your library")
                builder.setMessage("Are you sure you want to save this music to your library?")
                builder.setPositiveButton("Yes") { _, _ ->
                    firebaseOperations.addMusicToDatabase(musicObject!!, username)
                    finish()
                }
                builder.setNegativeButton("No") { _, _ ->
                    finish()
                }
                builder.show()
            }

        }
    }

    private fun createIdForMusic(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
    @SuppressLint("SetTextI18n")
    fun editTextOperations() {
        Log.i("Edit Text Operations", lyricsList.toString())
        var currentPage = binding.libraryLineTextView.text.toString().split(" ")[1].toInt() - 1

        binding.libraryNextButton.setOnClickListener {
            // Change early line string in the list to the new one
            saveEarlyProgressToList(currentPage)

            currentPage++
            if (currentPage > lyricsList!!.size - 1) {
                currentPage = lyricsList!!.size - 1
            }
            showOperations(currentPage)
        }
        binding.libraryBackButton.setOnClickListener {
            saveEarlyProgressToList(currentPage)

            currentPage--
            if (currentPage < 0) {
                currentPage = 0
            }
            showOperations(currentPage)
        }

    }

    private fun saveEarlyProgressToList(currentPage: Int) {
        binding.apply {
            val lyrics = libraryLyricsEditText.text.toString()
            val chords = libraryChordsEditText.text.toString()
            val rhythm = libraryRhythmsEditText.text.toString()
            lyricsList!![currentPage] = lyrics
            chordsList!![currentPage] = chords
            rhythmList!![currentPage] = rhythm
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showOperations(currentPage: Int) {
        binding.apply {
            libraryLineTextView.text = "Line ${currentPage + 1}"
            libraryLyricsEditText.setText(lyricsList!![currentPage])
            libraryChordsEditText.setText(chordsList!![currentPage])
            libraryRhythmsEditText.setText(rhythmList!![currentPage])
        }
    }


    @SuppressLint("SetTextI18n")
    fun loadMusicsToEditTexts(musicId: String?) {
        if (musicId == null) {
            return
        }
        firebaseDatabase.getReference("Musics").child(musicId).get().addOnSuccessListener {
            val music = it.getValue(Music::class.java)
            lyricsList = music?.lyrics?.split("|") as MutableList<String>?
            chordsList = music?.chords?.split("|") as MutableList<String>?
            rhythmList = music?.rhythms?.split("|") as MutableList<String>?
            musicObject = music

            // Show the music lines in different pages
            binding.apply {
                libraryLineTextView.text = "Line 1"
                libraryLyricsEditText.setText(lyricsList?.get(0) ?: "")
                libraryChordsEditText.setText(chordsList?.get(0) ?: "")
                libraryRhythmsEditText.setText(rhythmList?.get(0) ?: "")
            }
            return@addOnSuccessListener
        }
    }
}