package org.wentura.locoway.data.model

import java.time.LocalDate

sealed class ConnectionPagingModel {
    class UiConnection(val connection: Connection) : ConnectionPagingModel()

    class DateSeparator(val localDate: LocalDate) : ConnectionPagingModel()

    data object Divider : ConnectionPagingModel()
}
