package com.wentura.pkp_android.data

import com.wentura.pkp_android.api.BilkomService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(private val bilkomService: BilkomService) {
    suspend fun searchStations(query: String): List<Station> {
        return bilkomService.getStations(query).stations.filter { station ->
            !station.name.endsWith("-")
        }
    }
}
