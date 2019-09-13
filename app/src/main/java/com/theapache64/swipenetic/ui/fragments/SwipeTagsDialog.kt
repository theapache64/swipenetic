package com.theapache64.swipenetic.ui.fragments

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.LayoutSwipeTagAlertBinding
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.swipenetic.ui.adapters.SwipeTagsAdapter

object SwipeTagsDialog {

    fun create(
        context: Context,
        callback: Callback
    ): AlertDialog {

        val layoutInflater = LayoutInflater.from(context)
        val view = LayoutSwipeTagAlertBinding.inflate(layoutInflater, null, false)
        val dialog =
            AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme_DialogActivity))
                .setView(view.root)
                .setCancelable(false)
                .create()

        val adapter = SwipeTagsAdapter(context) { swipeTag ->
            callback.onSwipeTagSelected(swipeTag, dialog)
        }
        view.rvSwipeTags.adapter = adapter

        return dialog
    }

    interface Callback {
        fun onSwipeTagSelected(swipeOutTag: SwipeOutTag, dialog: Dialog)
    }
}