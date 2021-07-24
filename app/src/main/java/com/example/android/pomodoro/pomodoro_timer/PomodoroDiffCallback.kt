package com.example.android.pomodoro.pomodoro_timer

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil

class PomodoroDiffCallback :
    DiffUtil.ItemCallback<Timer>() {

    override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
        return oldItem.remainingMS == newItem.remainingMS &&
                oldItem.isStarted == newItem.isStarted
    }

    @Nullable
    @Override
    override fun getChangePayload(oldItem: Timer, newItem: Timer): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}