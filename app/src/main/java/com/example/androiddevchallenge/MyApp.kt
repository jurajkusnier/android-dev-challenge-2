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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.almostWhite
import com.example.androiddevchallenge.ui.theme.lightBackground
import com.example.androiddevchallenge.ui.theme.orange
import com.example.androiddevchallenge.ui.theme.red
import com.example.androiddevchallenge.ui.theme.whiteWithOpacity2

@ExperimentalAnimationApi
@Composable
fun MyApp(viewModel: MainActivityViewModel) {

    val state: TimerState by viewModel.state.observeAsState(TimerState.TimerDone)

    Surface {
        Column {
            Header()

            if (state is TimerState.TimerSetup) {
                TimeSelector(state.remainingTimeInSeconds) { viewModel.setTime(it) }
            } else {
                CountdownTimerText(state.remainingTimeInSeconds)
            }

            ButtonsRow(
                state = state,
                playPause = {
                    when (state) {
                        TimerState.TimerDone -> viewModel.resetTimer()
                        is TimerState.TimerRunning -> viewModel.pauseTimer()
                        else -> viewModel.startTimer()
                    }
                },
                stop = {
                    viewModel.resetTimer()
                }
            )
        }
    }
}

@Composable
fun CountdownTimerText(timeInSeconds: Int) {
    val h = ((timeInSeconds / 60 / 60) % 24).toString().padStart(2, '0')
    val m = ((timeInSeconds / 60) % 60).toString().padStart(2, '0')
    val s = (timeInSeconds % 60).toString().padStart(2, '0')
    Box(
        Modifier
            .height(BOX_SIZE * 5)
            .fillMaxWidth()
    ) {
        CentralLineBox()
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                h, fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(BOX_SIZE),
                textAlign = TextAlign.Center
            )
            Text(":", fontSize = 48.sp)
            Text(
                m, fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(BOX_SIZE),
                textAlign = TextAlign.Center
            )
            Text(":", fontSize = 48.sp)
            Text(
                s, fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(BOX_SIZE),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TimeSelector(
    initialTimeInSeconds: Int,
    setCountDown: (Int) -> Unit
) {
    var h by remember { mutableStateOf(0) }
    var m by remember { mutableStateOf(0) }
    var s by remember { mutableStateOf(0) }

    fun updateCountdown() {
        if (initialTimeInSeconds != s + m * 60 + h * 60 * 60) {
            setCountDown(s + m * 60 + h * 60 * 60)
        }
    }

    Box {
        CentralLineBox()
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TimeColumn(24) {
                h = it; updateCountdown()
            }
            Text(":", fontSize = 48.sp)
            TimeColumn(60) {
                m = it; updateCountdown()
            }
            Text(":", fontSize = 48.sp)
            TimeColumn(60) { s = it; updateCountdown() }
        }
        GradientBox()
    }
}

@Composable
fun ButtonsRow(state: TimerState, playPause: () -> Unit, stop: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        ResetButton(stop)
        PlayPauseButton(state, playPause)
        SoundButton()
    }
}

@Composable
fun CentralLineBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BOX_SIZE * 2)
            .height(BOX_SIZE)
            .background(lightBackground)
    ) {}
}

@Composable
fun GradientBox() {
    val boxPixelSize = with(LocalDensity.current) { BOX_SIZE.toPx() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BOX_SIZE * 5)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.background.copy(alpha = 0f),
                        MaterialTheme.colors.background.copy(alpha = 0.8f),
                        MaterialTheme.colors.background,
                    ),
                    boxPixelSize * 2.25f,
                    boxPixelSize * 0.25f
                )
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.background.copy(alpha = 0f),
                        MaterialTheme.colors.background.copy(alpha = 0.5f),
                        MaterialTheme.colors.background,
                    ),
                    boxPixelSize * 2.75f,
                    boxPixelSize * 4.75f
                )
            )
    ) {}
}

@Composable
fun Header() {
    Text(
        "TIMER",
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 32.dp)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            "HOURS",
            Modifier.width(BOX_SIZE),
            textAlign = TextAlign.Center,
            color = whiteWithOpacity2
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "MINUTES",
            Modifier.width(BOX_SIZE),
            textAlign = TextAlign.Center,
            color = whiteWithOpacity2
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "SECONDS",
            Modifier.width(BOX_SIZE),
            textAlign = TextAlign.Center,
            color = whiteWithOpacity2
        )
    }
    Box(
        Modifier
            .background(color = whiteWithOpacity2)
            .height(1.dp)
            .fillMaxWidth()
    ) {}
}

@Composable
fun PlayPauseButton(state: TimerState, playPause: () -> Unit) {
    Button(
        onClick = { playPause() },
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .height(64.dp)
            .width(200.dp)
            .padding(horizontal = 32.dp)
    ) {
        Box(
            modifier = if (state !is TimerState.TimerRunning)
                Modifier
                    .background(Brush.horizontalGradient(listOf(red, orange)))
                    .fillMaxWidth()
                    .fillMaxHeight() else
                Modifier
                    .background(almostWhite)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                when (state) {
                    is TimerState.TimerRunning -> "PAUSE"
                    TimerState.TimerDone -> "STOP"
                    is TimerState.TimerPaused -> "RESUME"
                    is TimerState.TimerSetup -> "START"
                },
                fontSize = 20.sp,
                color = if (state !is TimerState.TimerRunning) whiteWithOpacity2 else red
            )
        }
    }
}

@Composable
fun ResetButton(stop: () -> Unit) {
    IconButton(
        onClick = { stop() },
        modifier = Modifier
            .size(56.dp)
            .background(lightBackground, CircleShape)
    ) {
        Icon(
            Icons.Filled.Refresh,
            contentDescription = null,
            tint = whiteWithOpacity2
        )
    }
}

@Composable
fun SoundButton() {
    var isOn by remember { mutableStateOf(false) }
    IconButton(
        onClick = { isOn = !isOn },
        modifier = Modifier
            .size(56.dp)
            .background(lightBackground, CircleShape)
    ) {
        Icon(
            imageVector = if (isOn) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
            contentDescription = null,
            tint = whiteWithOpacity2
        )
    }
}
