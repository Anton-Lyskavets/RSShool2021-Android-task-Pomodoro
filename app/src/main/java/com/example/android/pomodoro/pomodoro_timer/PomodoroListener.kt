package com.example.android.pomodoro.pomodoro_timer

interface PomodoroListener {
    fun start(id: Int)
    fun stop(id: Int, currentMs: Long)
    fun delete(id: Int)
}