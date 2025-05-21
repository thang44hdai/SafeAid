package com.example.safeaid.screens.guide

import android.view.LayoutInflater
import com.example.androidtraining.databinding.ItemGuideStepBinding
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safeaid.core.response.GuideStepResponse

class StepAdapter(private val onStepClick: (GuideStepResponse) -> Unit) : 
    ListAdapter<GuideStepResponse, StepAdapter.StepViewHolder>(StepDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemGuideStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    class StepViewHolder(private val binding: ItemGuideStepBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(step: GuideStepResponse, onStepClick: (GuideStepResponse) -> Unit) {
            binding.apply {
                tvStepNumber.text = step.orderIndex.toString()
                tvStepTitle.text = step.title
                tvStepDescription.text = step.content
                root.setOnClickListener { onStepClick(step) }
            }
        }
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(getItem(position), onStepClick)
    }
}

class StepDiffCallback : DiffUtil.ItemCallback<GuideStepResponse>() {
    override fun areItemsTheSame(oldItem: GuideStepResponse, newItem: GuideStepResponse): Boolean {
        return oldItem.stepId == newItem.stepId
    }

    override fun areContentsTheSame(oldItem: GuideStepResponse, newItem: GuideStepResponse): Boolean {
        return oldItem == newItem
    }
}