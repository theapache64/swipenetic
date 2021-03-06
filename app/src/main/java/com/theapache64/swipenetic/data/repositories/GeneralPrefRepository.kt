package com.theapache64.swipenetic.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class GeneralPrefRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_IS_TILE_ADDED = "is_tile_added"
        private const val KEY_WORK_ALERT_ID = "work_alert_id"
    }

    fun setIsTileAdded(isTileAdded: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_TILE_ADDED, isTileAdded)
        }
    }

    fun isTileAdded(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_TILE_ADDED, false)
    }

    fun saveWorkId(workId: String?) {
        sharedPreferences.edit {
            putString(KEY_WORK_ALERT_ID, workId)
        }
    }

    fun getWorkId(): String? {
        return sharedPreferences.getString(KEY_WORK_ALERT_ID, null)
    }

}