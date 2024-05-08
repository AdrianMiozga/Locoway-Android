import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wentura.pkp_android.ui.PKPAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(onUpClick: () -> Unit = {}) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Strzelce Opolskie")
                Text("Gliwice")
            }
        }, navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "")
            }
        })
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Search")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PKPAndroidTheme {
        Search()
    }
}
