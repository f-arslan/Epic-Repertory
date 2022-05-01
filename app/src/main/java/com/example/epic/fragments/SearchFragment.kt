package com.example.epic.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epic.R
import com.example.epic.adapters.SearchAdapter
import com.example.epic.data.DataSource
import com.example.epic.data.Music
import com.example.epic.databinding.FragmentSearchBinding
import java.util.*


class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener {
    private lateinit var viewOfLayout: View
    private lateinit var binding: FragmentSearchBinding
    private var musicList = DataSource().loadMusics()
    private var adapter = SearchAdapter(musicList, this)
    private var tempMusicList: ArrayList<Music> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewOfLayout = inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentSearchBinding.bind(viewOfLayout)
        recycleViewInitialize()

        // Filter function
        filterSearch(inflater)

        return viewOfLayout
    }



    private fun filterSearch(inflater: LayoutInflater) {
        val typeface = ResourcesCompat.getFont(inflater.context, R.font.athiti_bold)
        binding.searchSearchView.setTypeFace(typeface)

        for (music in musicList) {
            tempMusicList.add(music)
        }

        binding.searchSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                tempMusicList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                for (music in musicList) {
                    if (music.title!!.lowercase(Locale.getDefault()).contains(searchText)) {
                        tempMusicList.add(music)
                    }
                }
                adapter = SearchAdapter(tempMusicList, this@SearchFragment)
                adapter.notifyDataSetChanged()
                binding.searchRecyclerView.adapter = adapter
                return true
            }
        })
    }

    private fun SearchView.setTypeFace(typeface: Typeface?) {
        val id = context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = this.findViewById<View>(id) as TextView
        textView.typeface = typeface
    }



    private fun recycleViewInitialize() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.apply {
            searchRecyclerView.layoutManager = linearLayoutManager
            searchRecyclerView.adapter = adapter
        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}