package com.theapache64.swipenetic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theapache64.swipenetic.databinding.ItemSwipeSessionBinding
import com.theapache64.swipenetic.models.SwipeSession

class SwipeSessionsAdapter(
    context: Context,
    private val swipeSessions: List<SwipeSession>,
    private val callback: (position: Int) -> Unit
) : RecyclerView.Adapter<SwipeSessionsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSwipeSessionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = swipeSessions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val swipeSession = swipeSessions[position]
        holder.binding.swipeSession = swipeSession
    }

    inner class ViewHolder(val binding: ItemSwipeSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback(layoutPosition)
            }
        }
    }
}