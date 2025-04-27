package com.example.safeaid.core.ui.recyclerview

import androidx.recyclerview.widget.ListAdapter
import com.example.safeaid.core.ui.recyclerview.adapter.RecyclerViewAdapterFactory
import com.example.safeaid.core.ui.recyclerview.core.AdapterBuilder
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder

inline fun <T : Any> adapterOf(
    adapterBuilder: AdapterBuilder<T>.() -> Unit
): ListAdapter<T, RecyclerViewHolder<T>> =
    AdapterBuilder<T>().apply(adapterBuilder).build(
        RecyclerViewAdapterFactory()
    ) as ListAdapter<T, RecyclerViewHolder<T>>
