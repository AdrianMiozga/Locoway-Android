package org.wentura.locoway.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.wentura.locoway.api.BilkomService
import org.wentura.locoway.data.model.Station

@Singleton
class StationRepository @Inject constructor(private val bilkomService: BilkomService) {
    suspend fun searchStations(query: String): List<Station> {
        return bilkomService.getStations(query).stations.filter { station ->
            !station.name.endsWith("-")
        }
    }
}
