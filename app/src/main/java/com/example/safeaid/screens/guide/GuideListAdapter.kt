//package com.example.safeaid.screens.guide
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.androidtraining.databinding.ItemGuideCategoryBinding
//import com.example.androidtraining.databinding.ItemGuideBinding
//import com.example.safeaid.core.response.Guide
//import com.example.safeaid.core.response.GuideCategoryResponse
//
//class GuideListAdapter(
//    private val onCategoryClick: (GuideCategoryResponse) -> Unit,
//    private val onGuideClick: (Guide) -> Unit
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    companion object {
//        private const val TYPE_CATEGORY = 0
//        private const val TYPE_GUIDE = 1
//    }
//
//    // Danh sách các item hiển thị (bao gồm cả category và guide)
//    private val items = mutableListOf<ListItem>()
//
//    // Danh sách tất cả các category
//    private var categories = listOf<GuideCategoryResponse>()
//
//    // Cập nhật danh sách categories và tạo lại danh sách items
//    fun submitList(newCategories: List<GuideCategoryResponse>) {
//        categories = newCategories
//        updateItems()
//    }
//
//    // Cập nhật danh sách items
//    private fun updateItems() {
//        items.clear()
//
//        categories.forEach { category ->
//            // Thêm category vào danh sách
//            items.add(ListItem.CategoryItem(category))
//
//            // Thêm tất cả các guide của category vào danh sách
//            category.guides.forEach { guide ->
//                items.add(ListItem.GuideItem(guide, category.categoryId))
//            }
//        }
//
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is ListItem.CategoryItem -> TYPE_CATEGORY
//            is ListItem.GuideItem -> TYPE_GUIDE
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            TYPE_CATEGORY -> {
//                val binding = ItemGuideCategoryBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                CategoryViewHolder(binding)
//            }
//            TYPE_GUIDE -> {
//                val binding = ItemGuideBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                GuideViewHolder(binding)
//            }
//            else -> throw IllegalArgumentException("Unknown view type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val item = items[position]) {
//            is ListItem.CategoryItem -> (holder as CategoryViewHolder).bind(item.category)
//            is ListItem.GuideItem -> (holder as GuideViewHolder).bind(item.guide)
//        }
//    }
//
//    inner class CategoryViewHolder(private val binding: ItemGuideCategoryBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(category: GuideCategoryResponse) {
//            binding.apply {
//                tvCategoryName.text = category.name
//                tvCategoryDescription.text = category.description
//                tvGuideCount.text = "${category.guides.size} hướng dẫn"
//
//                // Xử lý sự kiện click vào category
//                root.setOnClickListener {
//                    onCategoryClick(category)
//                }
//            }
//        }
//    }
//
//    inner class GuideViewHolder(private val binding: ItemGuideBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(guide: Guide) {
//            binding.apply {
//                tvTitle.text = guide.title
//                tvDescription.text = guide.description
//
//                // Xử lý sự kiện click vào guide
//                root.setOnClickListener {
//                    onGuideClick(guide)
//                }
//            }
//        }
//    }
//
//    // Sealed class đại diện cho các loại item trong danh sách
//    sealed class ListItem {
//        data class CategoryItem(val category: GuideCategoryResponse) : ListItem()
//        data class GuideItem(val guide: Guide, val parentCategoryId: String) : ListItem()
//    }
//}

package com.example.safeaid.screens.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.R
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.GuideCategoryResponse

class GuideListAdapter(
    private val onCategoryClick: (GuideCategoryResponse) -> Unit,
    private val onGuideClick: (Guide) -> Unit
) : ListAdapter<GuideCategoryResponse, GuideListAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guide_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val rvGuides: RecyclerView = itemView.findViewById(R.id.rvGuides)

        fun bind(category: GuideCategoryResponse) {
            tvCategoryName.text = category.name

            // Set click listener for the category
            itemView.setOnClickListener { onCategoryClick(category) }

            // Setup nested RecyclerView for guides within this category
            val guidesAdapter = GuidesAdapter(onGuideClick)
            rvGuides.apply {
                layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = guidesAdapter
            }
            guidesAdapter.submitList(category.guides)
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<GuideCategoryResponse>() {
        override fun areItemsTheSame(oldItem: GuideCategoryResponse, newItem: GuideCategoryResponse): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: GuideCategoryResponse, newItem: GuideCategoryResponse): Boolean {
            return oldItem == newItem
        }
    }
}

class GuidesAdapter(
    private val onGuideClick: (Guide) -> Unit
) : ListAdapter<Guide, GuidesAdapter.GuideViewHolder>(GuideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guide, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val guide = getItem(position)
        holder.bind(guide)
    }

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvGuideTitle: TextView = itemView.findViewById(R.id.tvGuideTitle)
        private val tvGuideDescription: TextView = itemView.findViewById(R.id.tvGuideDescription)
        // Add other views as needed

        fun bind(guide: Guide) {
            tvGuideTitle.text = guide.title
            tvGuideDescription.text = "Quy trình sơ cứu khi ${guide.title.toLowerCase()}"
            // Set other properties as needed

            // Set click listener
            itemView.setOnClickListener { onGuideClick(guide) }
        }
    }

    class GuideDiffCallback : DiffUtil.ItemCallback<Guide>() {
        override fun areItemsTheSame(oldItem: Guide, newItem: Guide): Boolean {
            return oldItem.guideId == newItem.guideId
        }

        override fun areContentsTheSame(oldItem: Guide, newItem: Guide): Boolean {
            return oldItem == newItem
        }
    }
}