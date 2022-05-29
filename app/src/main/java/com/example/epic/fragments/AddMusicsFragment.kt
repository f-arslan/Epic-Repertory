package com.example.epic.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.epic.FirebaseOperations
import com.example.epic.R
import com.example.epic.data.Music
import com.example.epic.databinding.AddMusicsFragmentBinding
import java.util.*

class AddMusicsFragment : Fragment() {


    private lateinit var viewOfLayout: View
    private lateinit var binding: AddMusicsFragmentBinding
    private lateinit var firebaseOperations: FirebaseOperations
    private val mutableMapOfMusic = mutableMapOf<Int, MutableList<String>>()
    private var lineNumber = 1
    private lateinit var tonesAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewOfLayout = inflater.inflate(R.layout.add_musics_fragment, container, false)
        binding = AddMusicsFragmentBinding.bind(viewOfLayout)
        firebaseOperations = FirebaseOperations(requireContext())

        initializeToneSpinner()
        pageOperations()
        val username = firebaseOperations.readUserNameFromFile()
        addMusicToDatabase(username)

        return viewOfLayout
    }

    @SuppressLint("SetTextI18n")
    private fun addMusicToDatabase(username: String) {
        binding.addMusicBtnAddDb.setOnClickListener {
            if (binding.addMusicTitleEditText.text.toString().isEmpty())
                return@setOnClickListener
            val lyrics = mutableListOf<String>()
            val chords = mutableListOf<String>()
            val rhythm = mutableListOf<String>()
            for (key in mutableMapOfMusic.keys) {
                lyrics.add(mutableMapOfMusic[key]!![0])
                chords.add(mutableMapOfMusic[key]!![1])
                rhythm.add(mutableMapOfMusic[key]!![2])
            }
            val music = Music(
                createIdForMusic(),
                binding.addMusicTitleEditText.text.toString(),
                binding.addMusicArtistEditText.text.toString(),
                "img",
                lyrics.joinToString("|"),
                chords.joinToString("|"),
                rhythm.joinToString("|"),
                binding.addMusicTonesTv.text.toString()
            )
            firebaseOperations.addMusicToDatabase(music, username)
            Toast.makeText(context, "Music added", Toast.LENGTH_SHORT).show()
            // Set mutableMapOfMusic elements to ""
            for (key in mutableMapOfMusic.keys) {
                mutableMapOfMusic[key]!![0] = ""
                mutableMapOfMusic[key]!![1] = ""
                mutableMapOfMusic[key]!![2] = ""
            }
            binding.addMusicTitleEditText.setText("")
            binding.addMusicArtistEditText.setText("")
            binding.addMusicTonesTv.setText("C")
            initializeToneSpinner()
            binding.addMusicLineTv.text = "Line 1"
            lineNumber = 1
        }
    }

    private fun createIdForMusic(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }

    @SuppressLint("SetTextI18n")
    private fun pageOperations() {

        binding.addMusicBtnNextPage.setOnClickListener {
            if (mutableMapOfMusic[lineNumber + 1] != null) {
                binding.apply {
                    addMusicLyricsEt.setText(mutableMapOfMusic[lineNumber + 1]?.get(0))
                    addMusicChordEt.setText(mutableMapOfMusic[lineNumber + 1]?.get(1))
                    addMusicRhythmEt.setText(mutableMapOfMusic[lineNumber + 1]?.get(2))
                }
                lineNumber++
                binding.addMusicLineTv.text = "Line $lineNumber"
                return@setOnClickListener
            }
            mutableMapOfMusic[lineNumber] = mutableListOf(
                binding.addMusicLyricsEt.text.toString(),
                binding.addMusicChordEt.text.toString(),
                binding.addMusicRhythmEt.text.toString()
            )
            lineNumber++
            binding.addMusicLineTv.text = "Line $lineNumber"
            if (mutableMapOfMusic[lineNumber] == null) {
                binding.apply {
                    addMusicLyricsEt.setText("")
                    addMusicChordEt.setText("")
                    addMusicRhythmEt.setText("")
                }
            }
        }
        binding.addMusicBtnBackPage.setOnClickListener {
            lineNumber--
            if (lineNumber == 0) {
                lineNumber = 1
                return@setOnClickListener
            }
            binding.apply {
                addMusicLineTv.text = "Line $lineNumber"
                addMusicLyricsEt.setText(mutableMapOfMusic[lineNumber]?.get(0).toString())
                addMusicChordEt.setText(mutableMapOfMusic[lineNumber]?.get(1).toString())
                addMusicRhythmEt.setText(mutableMapOfMusic[lineNumber]?.get(2).toString())
            }
        }
    }



    private fun initializeToneSpinner() {
        val tones = resources.getStringArray(R.array.tones)
        tonesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tones)
        binding.addMusicTonesTv.setAdapter(tonesAdapter)
    }

    @SuppressLint("SetTextI18n")
    override fun onPause() {
        super.onPause()
        for (key in mutableMapOfMusic.keys) {
            mutableMapOfMusic[key]!![0] = ""
            mutableMapOfMusic[key]!![1] = ""
            mutableMapOfMusic[key]!![2] = ""
        }
        binding.apply {
            addMusicTitleEditText.setText("")
            addMusicArtistEditText.setText("")
            addMusicTonesTv.setText("C")
            addMusicLyricsEt.setText("")
            addMusicChordEt.setText("")
            initializeToneSpinner()
            addMusicLineTv.text = "Line 1"
            lineNumber = 1
        }
    }
}