package org.wentura.locoway.data.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime

data class Passenger(
    val name: String = "",
    val hasREGIOCard: Boolean = false,
    val discount: Int = 0,
    val addedAt: String = LocalDateTime.now().toString(),
    @DocumentId val documentPath: String = "",
)
