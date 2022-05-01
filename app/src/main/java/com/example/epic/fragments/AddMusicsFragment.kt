package com.example.epic.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.epic.R

class AddMusicsFragment : Fragment() {


    private lateinit var viewModel: AddMusicsViewModel
    private lateinit var viewOfLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewOfLayout = inflater.inflate(R.layout.add_musics_fragment, container, false)
        return viewOfLayout
    }

}