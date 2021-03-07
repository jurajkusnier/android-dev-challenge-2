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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun TimeColumn(
    maxValue: Int,
    valueChanged: (Int) -> Unit
) {
    val boxSizePx = with(LocalDensity.current) { BOX_SIZE.toPx() }

    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()

    if (!listState.isScrollInProgress) {
        if (listState.firstVisibleItemIndex < maxValue + 2) {
            coroutineScope.launch {
                val absoluteIndex = listState.firstVisibleItemIndex + maxValue

                listState.scrollToItem(
                    listState.firstVisibleItemIndex + maxValue,
                    listState.firstVisibleItemScrollOffset
                )

                val newIndex = if (listState.firstVisibleItemScrollOffset > boxSizePx / 2)
                    absoluteIndex + 1
                else
                    absoluteIndex

                listState.animateScrollToItem(newIndex, 0)

                valueChanged(newIndex % maxValue)
            }
        } else if (listState.firstVisibleItemIndex > maxValue * 2 + 2) {
            coroutineScope.launch {
                val absoluteIndex = listState.firstVisibleItemIndex - maxValue * 2

                listState.scrollToItem(
                    absoluteIndex,
                    listState.firstVisibleItemScrollOffset
                )

                val newIndex = if (listState.firstVisibleItemScrollOffset > boxSizePx / 2)
                    absoluteIndex + 1
                else
                    absoluteIndex

                listState.animateScrollToItem(newIndex, 0)

                valueChanged(newIndex % maxValue)
            }
        } else {
            coroutineScope.launch {
                val absoluteIndex = listState.firstVisibleItemIndex
                val newIndex = if (listState.firstVisibleItemScrollOffset > boxSizePx / 2)
                    absoluteIndex + 1
                else
                    absoluteIndex

                if (listState.firstVisibleItemScrollOffset != 0) {
                    listState.animateScrollToItem(newIndex, 0)
                }

                valueChanged(newIndex % maxValue)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(BOX_SIZE * 5)
            .width(BOX_SIZE)
    ) {
        items((-2..(maxValue * 3 + 2)).toList()) { index ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(BOX_SIZE)
            ) {
                val textIndex =
                    if (index < 0) maxValue - index - 2 else index % maxValue
                Text(
                    textIndex.toString().padStart(2, '0'),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
