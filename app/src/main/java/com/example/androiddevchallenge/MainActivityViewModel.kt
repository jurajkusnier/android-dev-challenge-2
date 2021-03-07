/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _state = MutableLiveData<TimerState>()
    val state: LiveData<TimerState>
        get() = _state

    private var initialTimerInSeconds = 0
    private var timer: CountDownTimer? = null
    private var remainingTimeInMs: Long = 0

    init {
        _state.value = TimerState.TimerSetup(0)
    }

    fun setTime(time: Int) {
        if (initialTimerInSeconds == time) return
        initialTimerInSeconds = time
        remainingTimeInMs = 0
        _state.value = TimerState.TimerSetup(initialTimerInSeconds)
    }

    fun startTimer() {
        if (remainingTimeInMs <= 0) {
            remainingTimeInMs = initialTimerInSeconds * 1000L
        }
        timer = object : CountDownTimer(remainingTimeInMs, 250) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMs = millisUntilFinished
                _state.value = TimerState.TimerRunning((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                remainingTimeInMs = 0L
                _state.value = TimerState.TimerDone
            }
        }.apply { start() }
    }

    fun pauseTimer() {
        timer?.cancel()
        _state.value = TimerState.TimerPaused((remainingTimeInMs / 1000L).toInt())
    }

    fun resetTimer() {
        timer?.cancel()
        remainingTimeInMs = 0
        _state.value = TimerState.TimerSetup(initialTimerInSeconds)
    }
}

sealed class TimerState(val remainingTimeInSeconds: Int) {
    data class TimerSetup(private val value: Int) : TimerState(value)
    data class TimerRunning(private val value: Int) : TimerState(value)
    data class TimerPaused(private val value: Int) : TimerState(value)
    object TimerDone : TimerState(0)
}
