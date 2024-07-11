package com.wentura.pkp_android.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.data.TrainBrand

@Composable
fun TrainBrandCircle(trainBrand: TrainBrand, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(CircleShape)
            .background(trainBrand.displayColor)
            .height(40.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            trainBrand.displayName,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}
