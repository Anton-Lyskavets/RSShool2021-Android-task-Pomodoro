package com.example.android.pomodoro.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.pomodoro.*
import com.example.android.pomodoro.databinding.ActivityMainBinding
import com.example.android.pomodoro.pomodoro_service.ForegroundService
import com.example.android.pomodoro.pomodoro_timer.Timer
import com.example.android.pomodoro.pomodoro_timer.PomodoroAdapter
import com.example.android.pomodoro.pomodoro_timer.PomodoroListener

class MainActivity : AppCompatActivity(), PomodoroListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private val timerAdapter = PomodoroAdapter(this)
    private var nextId = 0
    private val timers = mutableListOf<Timer>()
    private var currentFinishTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = timerAdapter
            }

            addNewTimerButton.setOnClickListener {
                val time = binding.startTime.text.toString()
                addNewTimer(time)
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this, R.style.AlertDialog).apply {
            setTitle("Quit the application?")
            setPositiveButton("Yes") { _, _ ->
                currentFinishTime = 0L
                super.onBackPressed()
            }
            setNegativeButton("No") { _, _ ->
            }
            setCancelable(true)
        }.create().show()
    }

    private fun addNewTimer(startTime: String) {
        if (startTime != "" && startTime != "0"&& startTime != "00"&& startTime != "000"&& startTime != "0000"&& startTime != "00000"&& startTime != "000000"&& startTime != "0000000"&& startTime != "00000000") {
            timers.add(Timer(nextId++, startTime.toLong() * 1000 * 60, false))
            timerAdapter.submitList(timers.toList())
        }
        else{
            Toast.makeText(this,"INVALID INPUT", Toast.LENGTH_SHORT).show()
        }
    }

    override fun start(id: Int) {
        timers.map {
            if (it.isStarted) stop(it.id, it.remainingMS)
        }
        val index = timers.indexOf(timers.find { it.id == id })
        timers[index].run {
            isStarted = true
            finishTime = System.currentTimeMillis() + remainingMS
            currentFinishTime = finishTime
        }
        timerAdapter.submitList(timers.toList())
    }

    override fun stop(id: Int, currentMs: Long) {
        val index = timers.indexOf(timers.find { it.id == id })
        timers[index].run {
            remainingMS = if (isWorked) startMs
            else finishTime - System.currentTimeMillis()
            isStarted = false
        }
        currentFinishTime = 0L
        timerAdapter.submitList(timers.toList())
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, currentFinishTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        onAppForegrounded()
    }
}