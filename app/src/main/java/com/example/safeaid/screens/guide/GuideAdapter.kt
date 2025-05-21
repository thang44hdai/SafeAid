//package com.example.safeaid.screens.guide
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.androidtraining.databinding.ItemGuideBinding
//
//class GuideAdapter(private val onItemClick: (GuideItem) -> Unit) : ListAdapter<GuideItem, GuideAdapter.GuideViewHolder>(GuideDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
//        val binding = ItemGuideBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return GuideViewHolder(binding)
//    }
//
//    class GuideViewHolder(private val binding: ItemGuideBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: GuideItem, onItemClick: (GuideItem) -> Unit) {
//            binding.tvTitle.text = item.title
//            binding.tvDescription.text = item.description
//
//            binding.root.setOnClickListener { onItemClick(item) }
//        }
//    }
//
//    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
//        holder.bind(getItem(position), onItemClick)
//    }
//}
//
//class GuideDiffCallback : DiffUtil.ItemCallback<GuideItem>() {
//    override fun areItemsTheSame(oldItem: GuideItem, newItem: GuideItem): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: GuideItem, newItem: GuideItem): Boolean {
//        return oldItem == newItem
//    }
//}