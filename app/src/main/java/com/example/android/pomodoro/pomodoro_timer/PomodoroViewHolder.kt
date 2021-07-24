package com.example.android.pomodoro.pomodoro_timer

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.android.pomodoro.INTERVAL
import com.example.android.pomodoro.databinding.TimerItemBinding
import com.example.android.pomodoro.longTimeToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PomodoroViewHolder(
    private val binding: TimerItemBinding,
    private val listener: PomodoroListener
) :
    RecyclerView.ViewHolder(binding.root) {

    private var countDownTimer: CountDownTimer? = null

    fun bind(timer: Timer) {

        binding.stopwatchTimer.text = longTimeToString(timer.remainingMS)

        isVisible(true)

        if (timer.isWorked) finish(timer)
        else {
            binding.customTimer.setPeriod(timer.startMs)
            updateCustomTimer(timer.remainingMS)

            if (timer.isStarted) {
                startTimer(timer)
            } else stopTimer()
        }
        initButtonsListeners(timer)
    }

    private fun startTimer(timer: Timer) {
        binding.startStopTimerButton.text = STOP

        if (timer.isWorked) {
            timer.isWorked = false
            isVisible(true)
        }

        countDownTimer?.cancel()
        countDownTimer = getCountDownTimer(timer)
        countDownTimer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        binding.startStopTimerButton.text = START

        countDownTimer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {

        return object : CountDownTimer(timer.remainingMS, INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
                if (timer.isStarted) {
                    if (timer.remainingMS <= 0L) {
                        onFinish()
                    } else {
                        timer.remainingMS = timer.finishTime - System.currentTimeMillis()
                        binding.stopwatchTimer.text = longTimeToString(timer.remainingMS)
                        updateCustomTimer(timer.remainingMS)
                    }
                } else {
                    stopTimer()
                }
            }

            override fun onFinish() {
                finish(timer)
            }
        }
    }

    private fun finish(timer: Timer) {
        countDownTimer?.cancel()

        binding.run {
            startStopTimerButton.text = START
            stopwatchTimer.text = longTimeToString(timer.startMs)
            blinkingIndicator.isInvisible = true
            (blinkingIndicator.background as? AnimationDrawable)?.stop()
        }

        timer.run {
            remainingMS = timer.startMs
            isStarted = false
            isWorked = true
        }
        listener.stop(timer.id, timer.startMs)
        isVisible(false)
    }

    private fun updateCustomTimer(time: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.customTimer.setCurrent(time)
            delay(INTERVAL)
        }
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.startStopTimerButton.setOnClickListener {
            if (timer.isStarted) {
                binding.startStopTimerButton.text = START
                listener.stop(timer.id, timer.remainingMS)
            } else {
                startTimer(timer)
                listener.start(timer.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.startStopTimerButton.isClickable = false
            listener.delete(timer.id)
        }
    }

    private fun isVisible(visible: Boolean) {
        if (visible) {
            binding.layout.setBackgroundColor(Color.TRANSPARENT)
        } else {
            binding.layout.setBackgroundColor(Color.RED)
        }
        binding.customTimer.isInvisible = !visible
    }

    private companion object {
        private const val START = "START"
        private const val STOP = "STOP"
    }
}