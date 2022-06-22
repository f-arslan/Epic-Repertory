package com.example.epic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epic.FirebaseOperations
import com.example.epic.R
import com.example.epic.adapters.SearchAdapter
import com.example.epic.data.Music
import com.example.epic.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), SearchAdapter.OnItemClickListener {

    private lateinit var viewOfLayout: View
    private lateinit var binding: FragmentHomeBinding
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var firebaseOperations: FirebaseOperations
    private lateinit var adapter: SearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(viewOfLayout)
        firebaseOperations = FirebaseOperations(requireContext())
        // Get total data from database
        getTotalNumberOfData("Musics", binding.homeTvTotalMusicsCounter)
        getTotalNumberOfData("Users", binding.homeTvTotalUserCounter)
        getTotalNumberOfData("Comments", binding.homeTvTotalCommentCounter)


        // initialize the recycler view
        initializeRecyclerView()

        return viewOfLayout
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeRecyclerView() {
        val username = firebaseOperations.readUserNameFromFile()
        if (username != null) {
            firebaseDatabase.getReference("Users").child(username).child("musicList").get().addOnSuccessListener {
                val musicString: String = it.value.toString()
                val musicList = musicString.split(",")
                val tmpMusicList: MutableList<Music> = mutableListOf()
                firebaseDatabase.getReference("Musics").addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Error", error.toString())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        tmpMusicList.clear()
                        for (data in snapshot.children) {
                            val music = data.getValue(Music::class.java)
                            if (musicList.contains(music?.id)) {
                                tmpMusicList.add(music!!)
                            }
                        }
                        adapter = SearchAdapter(tmpMusicList, this@HomeFragment)
                        recycleViewInitialize()
                        binding.homeRvRepertory.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                })
            }
        }
    }

    private fun recycleViewInitialize() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.apply {
            homeRvRepertory.layoutManager = linearLayoutManager
            homeRvRepertory.adapter = adapter
        }
    }

    private fun getTotalNumberOfData(dbName: String, textView: TextView) {
        if (textView == binding.homeTvTotalCommentCounter) {
            firebaseDatabase.getReference(dbName).get().addOnSuccessListener {
                var counter = 0
                for (elem in it.children) {
                    for (elem2 in elem.children) {
                        counter++
                    }
                }
                textView.text = counter.toString()
            }
            return
        }
        firebaseDatabase.getReference(dbName).get().addOnSuccessListener {
            val totalData = it.childrenCount
            textView.text = totalData.toString()
        }
    }

    override fun onPause() {
        super.onPause()
        getTotalNumberOfData("Musics", binding.homeTvTotalMusicsCounter)
        getTotalNumberOfData("Users", binding.homeTvTotalUserCounter)
        getTotalNumberOfData("Comments", binding.homeTvTotalCommentCounter)
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        initializeRecyclerView()
    }

}