package com.example.kursywalut.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyForm(
    availableCodes: List<String>,
    selectedCode: String?,
    onCodeSelected: (String) -> Unit,
    onDaysBackChange: (String) -> Unit,
    customDateRange: Pair<Long, Long>?,
    onCustomDateRangeChange: (Pair<Long, Long>?) -> Unit,
    onFetchClick: () -> Unit, // nadal potrzebne, żeby pobrać dane przed pokazaniem wykresu
    onShowChart: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }

    val filteredCodes = if (textFieldValue.isEmpty()) {
        availableCodes
    } else {
        availableCodes.filter { it.contains(textFieldValue, ignoreCase = true) }
    }

    var selectedRange by remember { mutableStateOf("Last 7 days") }
    val predefinedRanges = listOf("Last 7 days", "Last 30 days", "Custom range")

    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- WALUTA ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        expanded = true
                        if (it != selectedCode) onCodeSelected("")
                    },
                    label = { Text("Currency code") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded && filteredCodes.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    filteredCodes.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                onCodeSelected(code)
                                textFieldValue = code
                                expanded = false
                            }
                        )
                    }
                }
            }

            // --- PRZEDZIAŁ CZASU ---
            Column {
                Text("Choose date range", style = MaterialTheme.typography.bodyMedium)
                predefinedRanges.forEach { range ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRange == range,
                            onClick = { selectedRange = range }
                        )
                        Text(range)
                    }
                }

                when (selectedRange) {
                    "Last 7 days" -> onDaysBackChange("7")
                    "Last 30 days" -> onDaysBackChange("30")
                    "Custom range" -> CustomDatePickers(
                        customDateRange = customDateRange,
                        onCustomDateRangeChange = onCustomDateRangeChange
                    )
                }
            }

            // --- PRZYCISK POKAŻ WYKRES ---
            if (!selectedCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onFetchClick()   // najpierw pobieramy dane
                        selectedCode?.let { onShowChart(it) } // potem pokazujemy wykres
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show the chart")
                }
            }
        }
    }
}

