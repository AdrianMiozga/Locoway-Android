
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme
import java.text.DateFormat
import java.util.Calendar

@Composable
fun Home(
    modifier: Modifier = Modifier, departureStation: String = "", arrivalStation: String = ""
) {
    var departureStationText by remember { mutableStateOf(departureStation) }
    var arrivalStationText by remember { mutableStateOf(arrivalStation) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            label = { Text(stringResource(R.string.departure_station)) },
            onValueChange = { departureStationText = it },
            value = departureStationText,
            singleLine = true,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text(stringResource(R.string.arrival_station)) },
            onValueChange = { arrivalStationText = it },
            value = arrivalStationText,
            singleLine = true,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        Row {
            OutlinedTextField(
                label = { Text(stringResource(R.string.departure_date)) },
                value = DateFormat.getDateInstance().format(Calendar.getInstance().time),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            )

            OutlinedTextField(
                label = { Text(stringResource(R.string.departure_time)) },
                value = DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(Calendar.getInstance().time),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            )
        }

        Button(
            onClick = {}, modifier = Modifier.padding(10.dp)
        ) {
            Text(stringResource(R.string.search))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    PKPAndroidTheme {
        Home()
    }
}
