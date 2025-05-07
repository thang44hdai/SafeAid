package com.example.safeaid.core.ui.recyclerview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.SnapHelper

object RecyclerViewBindingAdapter {
    @BindingAdapter("rvSnapHelper")
    @JvmStatic
    fun setRecyclerViewAdapter(recyclerView: RecyclerView, helper: SnapHelper?) {
        helper?.attachToRecyclerView(recyclerView)
    }

    @BindingAdapter("rvAdapter")
    @JvmStatic
    fun setRecyclerViewAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>?
    ) {
        recyclerView.adapter = adapter
    }


    @BindingAdapter(value = ["rvDecoration"])
    @JvmStatic
    fun setRecyclerViewDecoration(
        recyclerView: RecyclerView,
        decoration: ItemDecoration?
    ) {
        decoration?.let { itemDeco ->
            recyclerView.run {
                this.adapter?.let {
                    if (it.itemCount <= 2) removeItemDecoration(itemDeco)
                    else {
                        removeItemDecoration(itemDeco)
                        addItemDecoration(itemDeco)
                    }
                }
            }
        }
    }

    @BindingAdapter("rvItemTouchHelper")
    @JvmStatic
    fun setItemTouchHelper(
        recyclerView: RecyclerView,
        itemTouchHelper: ItemTouchHelper?
    ) {
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    @BindingAdapter("rvScrollToPosition")
    @JvmStatic
    fun setScrollPosition(
        recyclerView: RecyclerView,
        position: Int
    ) {
        recyclerView.layoutManager?.scrollToPosition(position)

    }

}