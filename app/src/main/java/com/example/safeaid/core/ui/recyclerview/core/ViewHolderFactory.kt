package com.example.safeaid.core.ui.recyclerview.core

import android.view.View

typealias ViewHolderCreator<VH> = (view: View) -> VH

interface ViewHolderFactory<out T : Any, out VH : RecyclerViewHolder<T>> {
    fun instantiate(view: View): VH
}