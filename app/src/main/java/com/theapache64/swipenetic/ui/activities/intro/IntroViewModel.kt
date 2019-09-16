package com.theapache64.swipenetic.ui.activities.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.repositories.GeneralPrefRepository
import javax.inject.Inject

class IntroViewModel @Inject constructor(
    private val prefRepo: GeneralPrefRepository
) : ViewModel() {

    private val isTileAdded = MutableLiveData<Boolean>()
    fun getIsTileAdded(): LiveData<Boolean> = isTileAdded

    fun checkIfTileAdded() {
        this.isTileAdded.value = prefRepo.isTileAdded()
    }

}