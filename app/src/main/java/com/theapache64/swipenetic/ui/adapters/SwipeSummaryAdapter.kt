package com.theapache64.swipenetic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theapache64.swipenetic.databinding.ItemSwipeSummaryBinding
import com.theapache64.swipenetic.models.SwipeSummary

class SwipeSummaryAdapter(
    context: Context,
    private val swipeSummaries: List<SwipeSummary>,
    private val callback: (position: Int) -> Unit
) : RecyclerView.Adapter<SwipeSummaryAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSwipeSummaryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = swipeSummaries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val swipeSummary = swipeSummaries[position]
        holder.binding.swipeSummary = swipeSummary
    }

    inner class ViewHolder(val binding: ItemSwipeSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback(layoutPosition)
            }
        }
    }
}