package com.example.kursywalut.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickers(
    customDateRange: Pair<Long, Long>?,
    onCustomDateRangeChange: (Pair<Long, Long>?) -> Unit
) {
    val todayMillis = System.currentTimeMillis()

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    // --- START DATE PICKER ---
    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = customDateRange?.first)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = state.selectedDateMillis ?: return@TextButton
                    if (selected > todayMillis) return@TextButton // przyszłe dni ignorowane
                    val end = customDateRange?.second
                    onCustomDateRangeChange(
                        if (end != null && selected > end) end to end
                        else selected to (end ?: selected)
                    )
                    showStartPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    // --- END DATE PICKER ---
    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = customDateRange?.second)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = state.selectedDateMillis ?: return@TextButton
                    if (selected > todayMillis) return@TextButton
                    val start = customDateRange?.first
                    onCustomDateRangeChange(
                        if (start != null && selected < start) start to start
                        else (start ?: selected) to selected
                    )
                    showEndPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    // --- PRZYCISKI WYWOŁUJĄCE PICKERY ---
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { showStartPicker = true }) {
            Text("Start: ${customDateRange?.first?.let { formatDate(it) } ?: "Choose"}")
        }
        Button(onClick = { showEndPicker = true }) {
            Text("End: ${customDateRange?.second?.let { formatDate(it) } ?: "Choose"}")
        }
    }
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}
