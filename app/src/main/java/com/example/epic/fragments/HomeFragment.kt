package com.example.epic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.epic.R
import com.example.epic.databinding.FragmentHomeBinding
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {

    private lateinit var viewOfLayout: View
    private lateinit var binding: FragmentHomeBinding
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(viewOfLayout)

        // Get total data from database
        getTotalNumberOfData("Musics", binding.homeTvTotalMusicsCounter)
        getTotalNumberOfData("Users", binding.homeTvTotalUserCounter)
        getTotalNumberOfData("Comments", binding.homeTvTotalCommentCounter)



        return viewOfLayout
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

}