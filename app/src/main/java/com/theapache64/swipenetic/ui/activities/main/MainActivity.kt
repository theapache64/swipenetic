package com.theapache64.swipenetic.ui.activities.main


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.databinding.ActivityMainBinding
import com.theapache64.swipenetic.models.SwipeSession
import com.theapache64.swipenetic.models.SwipeTag
import com.theapache64.swipenetic.ui.activities.summary.SummaryActivity
import com.theapache64.swipenetic.ui.adapters.SwipeSessionsAdapter
import com.theapache64.swipenetic.ui.fragments.SwipeTagsDialog
import com.theapache64.swipenetic.utils.DateUtils2
import com.theapache64.swipenetic.utils.Repeater
import com.theapache64.twinkill.logger.info
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.ui.widgets.LoadingView
import com.theapache64.twinkill.utils.Resource
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class MainActivity : BaseAppCompatActivity(), MainHandler, DatePickerDialog.OnDateSetListener {


    private lateinit var sessionsAdapter: SwipeSessionsAdapter
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val binding = bindContentView<ActivityMainBinding>(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        this.viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        binding.handler = this

        val lvSwipeSessions = binding.iContentMain.lvSwipeSessions
        lvSwipeSessions.setRetryCallback(object : LoadingView.RetryCallback {
            override fun onRetryClicked() {
                viewModel.loadSwipeSessions()
            }
        })

        binding.iContentMain.csrlMain.setOnRefreshListener {
            viewModel.loadSwipeSessions()
        }

        // Watching for swipe sessions
        viewModel.getSwipeSessions().observe(this, Observer {

            val rvSwipeSessions = binding.iContentMain.rvSwipeSessions

            when (it.status) {
                Resource.Status.LOADING -> {
                    binding.iContentMain.gContentMain.visibility = View.GONE
                    lvSwipeSessions.showLoading(R.string.loading_swipe_sessions)
                }
                Resource.Status.SUCCESS -> {
                    lvSwipeSessions.hideLoading()
                    val sessions = it.data!!
                    this.sessionsAdapter = SwipeSessionsAdapter(this, sessions) { position ->
                        info("Clicked")
                        val session = sessions[position]
                        if (session.type == Swipe.Type.OUT) {
                            setSwipeTag(position, session.startSwipe)
                        }
                    }
                    startUpdatingFirstItem(sessionsAdapter, sessions)
                    binding.iContentMain.gContentMain.visibility = View.VISIBLE
                    rvSwipeSessions.adapter = sessionsAdapter
                }
                Resource.Status.ERROR -> {
                    binding.iContentMain.gContentMain.visibility = View.GONE
                    lvSwipeSessions.showError(it.message!!)
                }
            }
        })

        // Watch for any swipe change
        viewModel.getSwipeChange().observe(this, Observer { ids ->
            // Data changed
            viewModel.loadSwipeSessions()
            viewModel.checkAndStartTotalInSwipeCounting()
        })


        startActivity(SummaryActivity.getStartIntent(this, Date()))
    }

    private fun setSwipeTag(position: Int, swipe: Swipe) {
        SwipeTagsDialog.create(this, object : SwipeTagsDialog.Callback {
            override fun onSwipeTagSelected(swipeTag: SwipeTag, dialog: Dialog) {
                swipe.tag = swipeTag
                viewModel.updateSwipe(swipe)
                dialog.dismiss()
                sessionsAdapter.notifyItemChanged(position)
            }
        }).show()
    }

    private val swipeUpdateRepeater = Repeater(1000)

    private fun startUpdatingFirstItem(
        sessionsAdapter: SwipeSessionsAdapter,
        sessions: List<SwipeSession>
    ) {

        val isToday = DateUtils.isToday(viewModel.currentDate.value!!.timeInMillis)
        if (isToday) {
            val lastSwipe = sessions.first()

            // Cancel previous one
            swipeUpdateRepeater.cancel()

            swipeUpdateRepeater.startExecute {

                lastSwipe.update(
                    lastSwipe.duration + 1000,
                    DateUtils2.tohmma(Date())
                )

                sessionsAdapter.notifyItemChanged(0)
            }
        } else {
            info("Not today!")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_show_summary -> {
                startActivity(
                    SummaryActivity.getStartIntent(
                        this,
                        viewModel.getCurrentDate()
                    )
                )
            }

            R.id.action_change_date -> {
                showChangeDateCalendar()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun showChangeDateCalendar() {

        val calendar = viewModel.currentDate
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            calendar.value!!.get(Calendar.YEAR),
            calendar.value!!.get(Calendar.MONTH),
            calendar.value!!.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.apply {
            maxDate = Date().time
        }

        datePickerDialog.show()
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.loadSwipeSessions(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        )
    }

    companion object {
        const val ID = R.id.MAIN_ACTIVITY_ID

        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }
    }
}
