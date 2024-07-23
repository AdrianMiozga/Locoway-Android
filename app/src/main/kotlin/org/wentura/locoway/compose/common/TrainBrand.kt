package org.wentura.locoway.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wentura.locoway.data.model.TrainBrand
import org.wentura.locoway.ui.LocowayTheme

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
            trainBrand.displayShortName,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun TrainBrandWide(trainBrand: TrainBrand, trainNumber: Long, modifier: Modifier = Modifier) {
    Box(modifier.clip(CircleShape).background(trainBrand.displayColor)) {
        Text(
            "${trainBrand.displayShortName} $trainNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp),
            color = Color.White,
        )
    }
}

@Preview
@Composable
fun TrainBrandCirclePreview() {
    LocowayTheme { TrainBrandCircle(trainBrand = TrainBrand.REG) }
}

@Preview
@Composable
fun TrainBrandWidePreview() {
    LocowayTheme { TrainBrandWide(trainBrand = TrainBrand.REG, trainNumber = 64326) }
}
