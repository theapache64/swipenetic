package com.theapache64.swipenetic.ui.activities.splash


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivitySplashBinding
import com.theapache64.swipenetic.ui.activities.intro.IntroActivity
import com.theapache64.swipenetic.ui.activities.main.MainActivity
import com.theapache64.twinkill.logger.info
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : BaseAppCompatActivity(), SplashHandler {

    private lateinit var viewModel: SplashViewModel
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val binding = bindContentView<ActivitySplashBinding>(R.layout.activity_splash)
        this.viewModel = ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)
        binding.viewModel = viewModel
        binding.handler = this

        // Watching activity launch command
        viewModel.getLaunchActivityEvent().observe(this, Observer { activityId ->

            when (activityId) {

                MainActivity.ID -> {
                    startActivity(MainActivity.getStartIntent(this))
                }

                IntroActivity.ID -> {
                    startActivity(IntroActivity.getStartIntent(this))
                }

                else -> throw IllegalArgumentException("Undefined activity id $activityId")
            }

            finish()

        })


        checkAppUpdate()
    }

    private fun checkAppUpdate() {

        val appUpdater = AppUpdaterUtils(this)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("theapache64", "swipenetic")
            .withListener(object : AppUpdaterUtils.UpdateListener {
                override fun onSuccess(update: Update?, isUpdateAvailable: Boolean?) {
                    info("Update found -> $update -> $isUpdateAvailable")
                    if (isUpdateAvailable == true && update != null) {
                        onUpdateAvailable(update.urlToDownload.toString())
                    } else {
                        startSplashTimer()
                    }
                }

                override fun onFailed(error: AppUpdaterError?) {
                    onUpdateFailed()
                }

            })

        appUpdater.start()
    }

    private fun onUpdateAvailable(url: String) {

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.title_update_available)
            .setMessage(R.string.msg_update_available)
            .setCancelable(false)
            .setPositiveButton(R.string.action_update) { _: DialogInterface, _: Int ->
                open(url)
            }
            .create()

        dialog.show()
    }

    private fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
        finish()
    }

    private fun startSplashTimer() {
        Handler().postDelayed({
            viewModel.goToNextScreen()
        }, SPLASH_DURATION)
    }

    fun onUpdateFailed() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.title_update_failed)
            .setMessage(R.string.msg_update_failed)
            .setCancelable(false)
            .setPositiveButton(R.string.action_retry) { _: DialogInterface, _: Int ->
                checkAppUpdate()
            }
            .create()

        dialog.show()
    }


    companion object {
        private const val SPLASH_DURATION = 1000L
    }

}
