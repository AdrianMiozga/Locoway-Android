package org.wentura.locoway.data.model

enum class Discount(val percentage: Double) {
    NORMAL(0.0),
    STUDENT(0.51),
    CHILDREN(0.37),
}
