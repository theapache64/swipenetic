package com.theapache64.swipenetic.ui.activities.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.BuildConfig
import com.theapache64.swipenetic.ui.activities.main.MainActivity
import com.theapache64.twinkill.utils.livedata.SingleLiveEvent
import javax.inject.Inject

class SplashViewModel @Inject constructor(

) : ViewModel() {

    val versionName = "v${BuildConfig.VERSION_NAME}"

    private val launchActivityEvent = SingleLiveEvent<Int>()

    fun getLaunchActivityEvent(): LiveData<Int> {
        return launchActivityEvent
    }

    fun goToNextScreen() {

        val activityId = MainActivity.ID

        // passing id with the finish notification
        launchActivityEvent.value = activityId
    }

    companion object {
        val TAG = SplashViewModel::class.java.simpleName
    }

}