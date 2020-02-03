package com.theapache64.swipenetic.ui.activities.main


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.databinding.ActivityMainBinding
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.swipenetic.models.SwipeSession
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
import com.theapache64.twinkill.utils.extensions.toast
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class MainActivity : BaseAppCompatActivity(), MainHandler, DatePickerDialog.OnDateSetListener {

    companion object {
        private const val CHANGE_START_TIME = 0
        private const val CHANGE_OUT_TAG = 1

        const val ID = R.id.MAIN_ACTIVITY_ID

        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }

    }


    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionsAdapter: SwipeSessionsAdapter
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        this.binding = bindContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        this.viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        binding.handler = this

        binding.iContentMain.rvSwipeSessions.itemAnimator = null

        val lvSwipeSessions = binding.iContentMain.lvSwipeSessions
        lvSwipeSessions.setRetryCallback(object : LoadingView.RetryCallback {
            override fun onRetryClicked() {
                viewModel.changeDate()
            }
        })

        binding.iContentMain.csrlMain.setOnRefreshListener {
            viewModel.changeDate()
        }

        // Watching for add session
        viewModel.getShowAddSessionDialog().observe(this, Observer { swipeType ->
            val title = if (swipeType == Swipe.Type.IN) {
                R.string.main_dialog_title_add_in_swipe
            } else {
                R.string.main_dialog_title_add_out_swipe
            }

            MaterialDialog(this).show {
                title(title)
                input(
                    hintRes = R.string.main_dialog_input_hint_enter_minutes,
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    allowEmpty = false
                ) { _: MaterialDialog, charSequence: CharSequence ->
                    viewModel.addSession(charSequence.toString().toInt())
                }
                positiveButton(R.string.action_add_session)
            }
        })

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
                        onSwipeSessionClicked(position, sessions[position])
                    }
                    startUpdatingFirstItem(sessionsAdapter, sessions)
                    binding.iContentMain.gContentMain.visibility = View.VISIBLE
                    rvSwipeSessions.adapter = sessionsAdapter
                    viewModel.checkAndStartTotalInSwipeCounting()
                }
                Resource.Status.ERROR -> {
                    viewModel.resetInTimeToZero()
                    binding.iContentMain.gContentMain.visibility = View.GONE
                    lvSwipeSessions.showError(it.message!!)
                }
            }
        })

        // Watch for any swipe change
        viewModel.getSwipeChange().observe(this, Observer {
            // Data changed
            viewModel.changeDate()
        })

        // Watch for date
        viewModel.getCurrentDateLiveData().observe(this, Observer {
            binding.toolbar.apply {
                post {
                    subtitle = com.theapache64.twinkill.utils.DateUtils.getReadableFormat(it.time)
                }
            }
        })
    }

    private fun onSwipeSessionClicked(position: Int, session: SwipeSession) {

        val disabledIndices = if (session.type == Swipe.Type.IN) {
            // thi is a IN session, disable tag changing option
            intArrayOf(CHANGE_OUT_TAG)
        } else {
            null
        }

        MaterialDialog(this).show {
            listItems(
                res = R.array.menu_swipe_session,
                disabledIndices = disabledIndices
            ) { materialDialog: MaterialDialog, index: Int, charSequence: CharSequence ->

                when (index) {

                    CHANGE_START_TIME -> {
                        // Change in time
                        changeStartTime(session, position)
                    }

                    CHANGE_OUT_TAG -> {
                        if (session.type == Swipe.Type.OUT) {
                            setSwipeTag(position, session.startSwipe)
                        }
                    }
                }
            }
        }


    }

    private fun changeStartTime(session: SwipeSession, position: Int) {

        val cal = Calendar.getInstance().apply {
            time = session.startSwipe.timestamp
        }

        val timePicker = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->

                // Changing swipe time
                val swipe = session.startSwipe.apply {
                    timestamp = cal.apply {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        cal.set(Calendar.MINUTE, minute)
                    }.time
                }

                // Updating swipe time
                viewModel.updateSwipe(swipe)
                sessionsAdapter.notifyItemChanged(position)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).apply {


            // Setting max time
            val maxCal = Calendar.getInstance().apply {
                time = session.endSwipe?.timestamp ?: Date()
            }

            setMaxTime(
                maxCal.get(Calendar.HOUR_OF_DAY),
                maxCal.get(Calendar.MINUTE),
                0
            )

            // Set min time
            if (viewModel.hasPreviousSwipeSessionForItemIn(position)) {
                val prevSession = viewModel.getSwipeSessionOrCrash(position + 1)
                val minCal = Calendar.getInstance().apply {
                    time = prevSession.startSwipe.timestamp
                }
                setMinTime(
                    minCal.get(Calendar.HOUR_OF_DAY),
                    minCal.get(Calendar.MINUTE),
                    0
                )
            }


        }

        timePicker.show(supportFragmentManager, null)
    }

    private fun setSwipeTag(position: Int, swipe: Swipe) {
        SwipeTagsDialog.create(this, object : SwipeTagsDialog.Callback {
            override fun onSwipeTagSelected(swipeOutTag: SwipeOutTag, dialog: Dialog) {
                swipe.outTag = swipeOutTag
                viewModel.updateSwipe(swipe)
                dialog.dismiss()
                sessionsAdapter.notifyItemChanged(position)
                if (position == 0 && DateUtils.isToday(viewModel.getCurrentDate().time)) {
                    // active out changed
                    viewModel.resetWorkAlert(swipeOutTag)
                }
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

            R.id.action_add_session -> {
                viewModel.showAddSessionDialog()
            }

            R.id.action_show_summary -> {
                if (viewModel.hasSwipeSessions()) {
                    startActivity(
                        SummaryActivity.getStartIntent(
                            this,
                            viewModel.getCurrentDate()
                        )
                    )
                } else {
                    toast(R.string.error_no_swipe_found)
                }
            }

            R.id.action_change_date -> {
                showChangeDateCalendar()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }


    /**
     * To show dialog to change current date
     */
    private fun showChangeDateCalendar() {
        val calendar = viewModel.currentDate
        val picker = DatePickerDialog.newInstance(
            this,
            calendar.value
        )

        picker.selectableDays = viewModel.getSelectableDates()
        picker.show(supportFragmentManager, null)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        viewModel.changeDate(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        )
    }


    override fun onPrevDateClicked() {
        viewModel.changeDateToPrev()
    }

    override fun onNextDateClicked() {
        viewModel.changeDateToNext()
    }

}
