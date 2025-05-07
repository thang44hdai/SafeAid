package com.example.safeaid.core.ui.recyclerview.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.safeaid.core.ui.recyclerview.core.AdapterFactory
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolderManager

class RecyclerViewAdapterFactory<T : Any> :
    AdapterFactory<T, ListAdapter<T, RecyclerViewHolder<T>>> {

    override fun create(
        recyclerViewHolderManager: RecyclerViewHolderManager<T, RecyclerViewHolder<T>>,
        itemDiffUtil: DiffUtil.ItemCallback<T>?
    ) = RecyclerViewAdapter(recyclerViewHolderManager, itemDiffUtil)
}