package com.example.kursywalut.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kursywalut.ui.CurrencyViewModel



@Composable
fun ChartScreen(
    uiState: CurrencyViewModel.UiState,
    code: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Exchange rate chart",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is CurrencyViewModel.UiState.Idle -> Text("No data in specified date range")
            is CurrencyViewModel.UiState.Loading -> Text("Loading...")
            is CurrencyViewModel.UiState.Error -> Text("Error: ${(uiState as CurrencyViewModel.UiState.Error).message}")
            is CurrencyViewModel.UiState.Success -> {
                val successState = uiState as CurrencyViewModel.UiState.Success
                if (successState.code == code) {
                    CurrencyChart(rates = successState.data, code = code)
                } else {
                    Text("No data for specified currency")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

