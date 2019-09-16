package com.theapache64.swipenetic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theapache64.swipenetic.databinding.ItemSwipeTagBinding
import com.theapache64.swipenetic.models.SwipeOutTag

class SwipeTagsAdapter(
    context: Context,
    private val callback: (swipeOutTag: SwipeOutTag) -> Unit
) : RecyclerView.Adapter<SwipeTagsAdapter.ViewHolder>() {

    private val swipeTags = SwipeOutTag.values()
    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSwipeTagBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = swipeTags.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val swipeTag = swipeTags[position]
        holder.binding.swipeOutTag = swipeTag
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