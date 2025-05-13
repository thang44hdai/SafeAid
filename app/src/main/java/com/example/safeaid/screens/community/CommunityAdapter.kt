package com.example.safeaid.screens.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemPostBinding
import com.example.safeaid.screens.community.data.PostDto

class CommunityAdapter(
    private var items: List<PostDto>,
    private val onCommentsClick: (PostDto) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.PostViewHolder>() {

    fun updateList(newList: List<PostDto>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class PostViewHolder(private val binding: ItemPostBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDto) {
            //Dùng Glide để load ảnh avatar nếu là null thì dùng ảnh mặc định
            Glide.with(binding.ivAvatar.context)
                .load(item.user.profile_image_path ?: R.drawable.default_avt)
                .placeholder(R.drawable.default_avt)
                .into(binding.ivAvatar)

            // header
            binding.tvUser.text = item.user.username
            // Lấy phần HH:mm từ created_at ISO
            binding.tvTime.text = item.created_at
                .substringAfter('T')
                .substringBefore('.')  // ví dụ "08:13:11" → giữ nguyên hoặc chỉ HH:mm

            // title & content
//            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content

            // ảnh media đầu tiên (nếu có)
            if (item.media.isNotEmpty()) {
                binding.ivMedia.visibility = View.VISIBLE
                val media = item.media[0]
                // nếu dùng Glide, nhớ thêm dependency và cấp quyền INTERNET
                Glide.with(binding.ivMedia.context)
                    .load(/* nếu media_link chỉ là path thì prepend base URL */ media.media_link)
                    .into(binding.ivMedia)
            } else {
                binding.ivMedia.visibility = View.GONE
            }

            // stats
            binding.tvLikes.text    = item.like_count.toString()
            binding.tvComments.text = item.comment_count.toString()
//            binding.tvViews.text    = item.view_count.toString()

            // xử lý click vào comments
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
