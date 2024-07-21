package com.wentura.pkp_android.api

import com.wentura.pkp_android.data.model.BilkomSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BilkomService {
    @GET("stacje/szukaj")
    suspend fun getStations(
        @Query("q") query: String,
        @Query("source") source: String = "FROMSTATION",
    ): BilkomSearchResponse

    companion object {
        private const val BASE_URL = "https://bilkom.pl/"

        fun create(): BilkomService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BilkomService::class.java)
        }
    }
}
