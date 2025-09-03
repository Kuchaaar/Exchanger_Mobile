package com.example.kursywalut.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("currency")
    suspend fun getRates(
        @Query("code") code: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<CurrencyRateDto>

    @GET("available_codes")
    suspend fun getAvailableCodes(): List<String>
}