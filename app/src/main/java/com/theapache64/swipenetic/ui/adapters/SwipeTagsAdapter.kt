package com.theapache64.swipenetic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theapache64.swipenetic.databinding.ItemSwipeTagBinding
import com.theapache64.swipenetic.models.SwipeTag

class SwipeTagsAdapter(
    context: Context,
    private val callback: (swipeTag: SwipeTag) -> Unit
) : RecyclerView.Adapter<SwipeTagsAdapter.ViewHolder>() {

    private val swipeTags = SwipeTag.values()
    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSwipeTagBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = swipeTags.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val swipeTag = swipeTags[position]
        holder.binding.swipeTag = swipeTag
    }

    inner class ViewHolder(val binding: ItemSwipeTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback(swipeTags[layoutPosition])
            }
        }
    }
}