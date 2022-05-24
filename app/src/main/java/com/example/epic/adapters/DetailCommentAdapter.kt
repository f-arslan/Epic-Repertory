package com.example.epic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.epic.R
import com.example.epic.data.Comment

class DetailCommentAdapter(
    private val comments: List<Comment>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<DetailCommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailCommentAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_comment, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DetailCommentAdapter.ViewHolder, position: Int) {
        val currentItem = comments[position]
        holder.tvUsername.text = currentItem.userName
        holder.tvComment.text = currentItem.content
        holder.tvDate.text = currentItem.date
    }

    override fun getItemCount() = comments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvUsername: TextView = itemView.findViewById(R.id.item_comment_username)
        val tvComment: TextView = itemView.findViewById(R.id.item_comment_content)
        val tvDate: TextView = itemView.findViewById(R.id.item_comment_date)

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