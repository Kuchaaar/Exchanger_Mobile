package com.example.kursywalut.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kursywalut.ui.CurrencyViewModel
import kotlinx.coroutines.launch
import com.example.kursywalut.R

@Composable
fun CurrencyScreen(
    vm: CurrencyViewModel = viewModel(),
    onShowChart: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()

    var daysBack by remember { mutableStateOf("7") }
    var customDateRange by remember { mutableStateOf<Pair<Long, Long>?>(null) }

    val baseUrl = "http://10.0.2.2:8080/"
    val pythonUrl = "http://10.0.2.2:8000/"

    val availableCodes by vm.availableCodes.collectAsState()
    var selectedCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.fetchAvailableCodes(baseUrl)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_currency_logo),
                contentDescription = "Logo aplikacji",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Exchanger",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        CurrencyForm(
            availableCodes = availableCodes,
            selectedCode = selectedCode,
            onCodeSelected = { selectedCode = it },
            onDaysBackChange = { daysBack = it },
            customDateRange = customDateRange,
            onCustomDateRangeChange = { customDateRange = it },
            onFetchClick = {
                scope.launch {
                    val code = selectedCode ?: return@launch
                    if (customDateRange != null) {
                        vm.fetch(
                            baseUrl = pythonUrl,
                            code = code,
                            startDate = customDateRange!!.first,
                            endDate = customDateRange!!.second
                        )
                    } else {
                        val days = daysBack.toIntOrNull() ?: 7
                        vm.fetch(baseUrl = pythonUrl, code = code, daysBack = days)
                    }
                }
            },
            onShowChart = { code ->
                scope.launch {
                    if (customDateRange != null) {
                        vm.fetch(
                            baseUrl = pythonUrl,
                            code = code,
                            startDate = customDateRange!!.first,
                            endDate = customDateRange!!.second
                        )
                    } else {
                        val days = daysBack.toIntOrNull() ?: 7
                        vm.fetch(baseUrl = pythonUrl, code = code, daysBack = days)
                    }
                    onShowChart(code)
                }
            }
        )
    }
}
