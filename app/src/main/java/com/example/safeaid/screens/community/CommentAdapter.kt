package com.example.safeaid.screens.community

import com.example.safeaid.screens.community.data.CommentItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemCommentBinding

class CommentAdapter(
    private var commentList: List<CommentItem>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentItem) {
            binding.tvUserName.text = item.userName
            binding.tvCommentTime.text = item.time
            binding.tvCommentContent.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int = commentList.size

    fun getData() = commentList

    fun updateData(newList: List<CommentItem>) {
        commentList = newList
        notifyDataSetChanged()
    }
}
