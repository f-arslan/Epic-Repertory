package com.example.epic.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.epic.DetailActivity
import com.example.epic.FirebaseOperations
import com.example.epic.R
import com.example.epic.data.Music
import com.example.epic.fragments.HomeFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class SearchAdapter(
    private val musicList: MutableList<Music>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var firebaseOperations: FirebaseOperations


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_music, parent, false)
        return ViewHolder(itemView)
    }

    private fun getImageFromStorage(imageName: String, holder: ViewHolder) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("images/$imageName.png")
        val localFile = File.createTempFile(imageName, "png")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.imageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = musicList[position]
        firebaseOperations = FirebaseOperations(context = holder.itemView.context)
        if (listener is HomeFragment) {
            holder.trashImageView.visibility = View.VISIBLE
            holder.trashImageView.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("Delete")
                builder.setMessage("Are you sure you want to delete this song?")
                builder.setPositiveButton("Yes") { _, _ ->
                    firebaseDatabase.getReference("Musics").child(currentItem.id.toString())
                        .removeValue()
                    val username = firebaseOperations.readUserNameFromFile()
                    firebaseDatabase.getReference("Users").child(username.toString())
                        .child("musicList").get().addOnSuccessListener {
                        val musicList = it.value.toString().split(",")
                        val newMusicList = StringBuilder()
                        for (music in musicList) {
                            if (music != currentItem.id.toString()) {
                                newMusicList.append(music)
                                newMusicList.append(",")
                            }
                        }
                        firebaseDatabase.getReference("Users").child(username.toString())
                            .child("musicList").setValue(newMusicList.toString())
                    }
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        }
        currentItem.cover?.let { getImageFromStorage(it, holder) }
        holder.titleTextView.text = currentItem.title
        holder.artistTextView.text = currentItem.artist
        holder.constraintLayout.setOnClickListener {
            val intent = Intent(holder.constraintLayout.context, DetailActivity::class.java)
            intent.putExtra("MUSIC_ID", currentItem.id)
            startActivity(holder.constraintLayout.context, intent, null)
        }
    }

    override fun getItemCount() = musicList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val artistTextView: TextView = itemView.findViewById(R.id.item_artist)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
        val trashImageView: ImageView = itemView.findViewById(R.id.item_trash)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}