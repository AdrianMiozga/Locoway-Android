package com.wentura.pkp_android.data.model

import androidx.compose.ui.graphics.Color

enum class TrainBrand(
    val displayName: String,
    val displayShortName: String,
    val displayColor: Color
) {
    REG("REG", "R", Color(0xFFE50000)),
    IC("IC", "IC", Color(0xFFF47216)),
    TLK("TLK", "TLK", Color(0xFF2919A0)),
    EIC("EIC", "EIC", Color(0xFF7B7979)),
    EIP("EIP", "EIP", Color(0xFF0B2950)),
}
