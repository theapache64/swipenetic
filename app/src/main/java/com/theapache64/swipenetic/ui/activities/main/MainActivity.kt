package com.theapache64.swipenetic.ui.activities.main


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivityMainBinding
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class MainActivity : BaseAppCompatActivity(), MainHandler, DatePickerDialog.OnDateSetListener {


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

        // Watching for swipe sessions


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        viewModel.setNewDate(
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
