package com.example.android.pomodoro.pomodoro_timer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.android.pomodoro.databinding.TimerItemBinding

class PomodoroAdapter(
    private val listener: PomodoroListener
)
    : ListAdapter<Timer, PomodoroViewHolder>(PomodoroDiffCallback())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PomodoroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerItemBinding.inflate(layoutInflater, parent, false)
        return PomodoroViewHolder(binding,listener)
    }

    override fun onBindViewHolder(holder: PomodoroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
