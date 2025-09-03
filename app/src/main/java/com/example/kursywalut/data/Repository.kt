package com.example.kursywalut.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyRepository(private val baseUrl: String) {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val client by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl.ensureEndsWithSlash())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CurrencyApi::class.java)
    }

    private val df: DateTimeFormatter = DateTimeFormatter.ISO_DATE

    suspend fun getByDateRange(code: String, startDate: Long, endDate: Long): List<CurrencyRateDto> {
        val sdf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = java.time.Instant.ofEpochMilli(startDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val end = java.time.Instant.ofEpochMilli(endDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()

        return api.getRates(
            code = code,
            startDate = start.format(sdf),
            endDate = end.format(sdf)
        ).sortedBy { it.date }
    }

    suspend fun getLastDays(code: String, daysBack: Int): List<CurrencyRateDto> {
        val end = LocalDate.now()
        val start = end.minusDays(daysBack.toLong())
        return api.getRates(
            code = code,
            startDate = start.format(df),
            endDate = end.format(df)
        ).sortedBy { it.date }
    }

    suspend fun getAvailableCodes(): List<String> {
        return api.getAvailableCodes()
    }

    private fun String.ensureEndsWithSlash(): String =
        if (this.endsWith("/")) this else "$this/"
}
