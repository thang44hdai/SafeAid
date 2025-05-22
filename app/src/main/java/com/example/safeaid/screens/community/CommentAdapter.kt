package com.example.safeaid.screens.community

import android.content.Context
import com.example.safeaid.screens.community.data.CommentItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemCommentBinding
import com.example.safeaid.core.utils.UserManager
import javax.inject.Inject

class CommentAdapter(
    private var commentList: List<CommentItem>,
    private val userManager: UserManager,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {



    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentItem) {
            binding.tvUserName.text = item.userName
            binding.tvCommentTime.text = item.time
            binding.tvCommentContent.text = item.content

            // Load avatar for this specific user
            loadUserAvatar(binding.root.context, item, binding)


            // Set up long click listener for comment deletion
            val currentUsername = userManager.getUsername(binding.root.context)
            val isCurrentUser = item.userName == currentUsername || item.userName == "Bạn"

            binding.cardComment.setOnLongClickListener { view ->
                if (isCurrentUser) {
                    showPopupMenu(view, item.id)
                    true
                } else {
                    false
                }
            }
        }

        private fun showPopupMenu(view: View, commentId: String) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_comment_options)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete_comment -> {
                        onDeleteClick(commentId)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        private fun loadUserAvatar(context: Context, comment: CommentItem, binding: ItemCommentBinding) {
            val currentUsername = userManager.getUsername(context)

            // Check if the comment has a profile image path
            if (!comment.profileImagePath.isNullOrEmpty()) {
                // Load the user's custom avatar
                Glide.with(context)
                    .load(comment.profileImagePath)
                    .placeholder(R.drawable.default_avt)
                    .error(R.drawable.default_avt)
                    .into(binding.ivUserAvatar)
            }
            // If this is current user but comment doesn't have profile image
            else if (comment.userName == currentUsername || comment.userName == "Bạn") {
                val avatarUrl = userManager.getAvatarUrl(context)
                if (avatarUrl.isNotEmpty()) {
                    Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(R.drawable.default_avt)
                        .error(R.drawable.default_avt)
                        .into(binding.ivUserAvatar)
                } else {
                    binding.ivUserAvatar.setImageResource(R.drawable.default_avt)
                }
            } else {
                // For other users with no profile image
                binding.ivUserAvatar.setImageResource(R.drawable.default_avt)
            }
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

    fun removeItem(commentId: String) {
        val position = commentList.indexOfFirst { it.id == commentId }
        if (position != -1) {
            val newList = commentList.toMutableList()
            newList.removeAt(position)
            commentList = newList
            notifyItemRemoved(position)
        }
    }
}
