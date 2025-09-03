package com.example.kursywalut.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursywalut.data.CurrencyRateDto
import com.example.kursywalut.data.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val data: List<CurrencyRateDto>, val code: String): UiState()
        data class Error(val message: String): UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // --- dostępne kody walut ---
    private val _availableCodes = MutableStateFlow<List<String>>(emptyList())
    val availableCodes: StateFlow<List<String>> = _availableCodes

    fun fetch(
        baseUrl: String,
        code: String,
        daysBack: Int? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val repo = CurrencyRepository(baseUrl.ensureEndsWithSlash())
                val data = if (startDate != null && endDate != null) {
                    repo.getByDateRange(
                        code = code,
                        startDate = startDate,
                        endDate = endDate
                    )
                } else {
                    repo.getLastDays(
                        code = code,
                        daysBack = daysBack ?: 7
                    )
                }
                _uiState.value = UiState.Success(data,code)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchAvailableCodes(baseUrl: String) {
        viewModelScope.launch {
            try {
                val repo = CurrencyRepository(baseUrl.ensureEndsWithSlash())
                val codes = repo.getAvailableCodes()
                // --- sortowanie alfabetyczne ---
                _availableCodes.value = codes.sorted()
            } catch (e: Exception) {
                _availableCodes.value = emptyList()
            }
        }
    }

    private fun String.ensureEndsWithSlash(): String =
        if (this.endsWith("/")) this else "$this/"
}
