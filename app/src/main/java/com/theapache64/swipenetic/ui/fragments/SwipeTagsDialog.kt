package com.theapache64.swipenetic.ui.fragments

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.databinding.LayoutSwipeTagAlertBinding
import com.theapache64.swipenetic.ui.adapters.SwipeTagsAdapter

object SwipeTagsDialog {

    fun create(context: Context, swipe: Swipe): AlertDialog {

        val layoutInflater = LayoutInflater.from(context)
        val view = LayoutSwipeTagAlertBinding.inflate(layoutInflater, null, false)
        val adapter = SwipeTagsAdapter(context) {

        }
        view.rvSwipeTags.adapter = adapter

        return AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme_DialogActivity))
            .setView(view.root)
            .create()
    }
}