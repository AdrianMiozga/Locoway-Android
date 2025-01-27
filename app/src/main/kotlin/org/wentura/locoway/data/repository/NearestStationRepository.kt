package org.wentura.locoway.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.wentura.locoway.api.KoleoService
import org.wentura.locoway.data.model.Location

@Singleton
class NearestStationRepository @Inject constructor(private val koleoService: KoleoService) {

    suspend fun getNearestStation(latitude: Double, longitude: Double): String {
        return koleoService.getNearestStation(Location(latitude, longitude)).name
    }
}
