package org.wentura.locoway.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class LocationServiceBroadcastMonitor
@Inject
constructor(
    private val context: Context,
    coroutineScope: CoroutineScope,
    private val locationManager: LocationManager =
        context.getSystemService(LocationManager::class.java),
) {
    val isEnabled: SharedFlow<Boolean> =
        callbackFlow {
                trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

                val receiver =
                    object : BroadcastReceiver() {
                        override fun onReceive(broadcastContext: Context?, intent: Intent?) {
                            if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                                trySend(
                                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                            }
                        }
                    }

                context.registerReceiver(
                    receiver,
                    IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
                )

                trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

                awaitClose { context.unregisterReceiver(receiver) }
            }
            .distinctUntilChanged()
            .conflate()
            .flowOn(Dispatchers.IO)
            .shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)
}
