package com.example.kursywalut.data

data class CurrencyRateDto(
    val date: String,
    val mid: Double,
    val internetAnswer: String
)