package com.example.safeaid.screens.community

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemPostBinding
import com.example.safeaid.screens.community.data.PostDto
import kotlin.math.log

class CommunityAdapter(
    private var items: List<PostDto>,
    private val onCommentsClick: (PostDto) -> Unit,
    private val onLikeToggle: (PostDto, Boolean) -> Unit,
    private val onShareClick: (PostDto) -> Unit,
) : RecyclerView.Adapter<CommunityAdapter.PostViewHolder>() {

    fun updateList(newList: List<PostDto>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class PostViewHolder(private val binding: ItemPostBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDto) {
            Glide.with(binding.ivAvatar.context)
                .load(item.user.profile_image_path ?: R.drawable.default_avt)
                .placeholder(R.drawable.default_avt)
                .into(binding.ivAvatar)

            binding.tvUser.text = item.user.username
            binding.tvTime.text = item.time_ago
            binding.tvContent.text = item.content

            if (item.media.isNotEmpty()) {
                binding.ivMedia.visibility = View.VISIBLE
                val media = item.media[0]
                Glide.with(binding.ivMedia.context)
                    .load(media.media_link)
                    .into(binding.ivMedia)

                binding.ivMedia.setOnClickListener {
                    val context = binding.ivMedia.context
                    val intent = Intent(context, ImageViewerActivity::class.java).apply {
                        putExtra("image_url", media.media_link)
                    }
                    context.startActivity(intent)
                }
            } else {
                binding.ivMedia.visibility = View.GONE
            }

            binding.tvLikes.text    = item.like_count.toString()
            binding.tvLikes.setCompoundDrawablesWithIntrinsicBounds(
                if (item.liked_by_user) R.drawable.ic_liked else R.drawable.ic_like,
                0, 0, 0
            )

            binding.tvLikes.setOnClickListener {
                val pos = bindingAdapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
                val post = items[pos]
                post.liked_by_user = if (post.liked_by_user) false else true
                post.like_count += if (post.liked_by_user) +1 else -1
                notifyItemChanged(pos)
                onLikeToggle(post, post.liked_by_user)
            }
            binding.tvComments.text = item.comment_count.toString()

            binding.tvShares.setOnClickListener {
                onShareClick(item)
            }

            binding.tvComments.setOnClickListener { onCommentsClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
