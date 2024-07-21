package com.wentura.pkp_android.api

import com.wentura.pkp_android.data.KoleoPriceResponse
import com.wentura.pkp_android.data.KoleoSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KoleoService {
    @Headers("Accept: application/json")
    @GET("connections")
    suspend fun getConnections(
        @Query("query[date]") dateTime: String,
        @Query("query[start_station]") departureStation: String,
        @Query("query[end_station]") arrivalStation: String,
        @Query("query[only_direct]") onlyDirect: Boolean = true,
        @Query("query[brand_ids][]") brandIds: List<Int> = listOf(1, 2, 3, 29, 28),
    ): KoleoSearchResponse

    @GET("prices/{price_id}")
    suspend fun getPrices(
        @Path("price_id") priceId: Long,
    ): KoleoPriceResponse

    companion object {
        private const val BASE_URL = "https://koleo.pl/pl/"

        fun create(): KoleoService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KoleoService::class.java)
        }
    }
}
