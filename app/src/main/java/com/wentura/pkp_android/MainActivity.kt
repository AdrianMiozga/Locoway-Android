package com.wentura.pkp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wentura.pkp_android.ui.PKPApp
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PKPAndroidTheme {
                PKPApp()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    PKPAndroidTheme {
        PKPApp()
    }
}
