package com.example.epic

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.epic.data.Music
import com.example.epic.databinding.ActivityYouTubeBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.firebase.database.FirebaseDatabase

class YouTubeActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityYouTubeBinding
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var youTubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYouTubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicId = intent.getStringExtra("MUSIC_ID")
        var youtubeLink: String
        if (musicId != null) {
            Log.i("MUSIC_ID", musicId)
        }
        // Get data from db
        firebaseDatabase.getReference("Musics").get().addOnSuccessListener {
            for (music in it.children) {
                val musicData = music.getValue(Music::class.java)
                if (musicData?.id == musicId) {
                    if (musicData != null) {
                        youtubeLink = musicData.youtubeLink.toString()
                        binding.apply {
                            detailTvTitle.text = musicData.title
                        }
                        playerOperation(youtubeLink)
                    }
                    break
                }
            }
        }


    }

    private fun playerOperation(youtubeLink: String) {
        val parsedLink = youtubeLink.split("=")[1]
        Log.i("parsedLink", parsedLink)

        // Youtube Player Initialize
        youTubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1?.loadVideo(parsedLink)
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(this@YouTubeActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.youtubePlayButton.setOnClickListener {
            binding.youtubePlayer.initialize(getString(R.string.youtube_api_key), youTubePlayerInit)
        }
    }

}