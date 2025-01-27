package com.example.s5app.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun NoInternetConnectionScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),) {
            Text(
                text = "No Internet Connection",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun NoInternetConnectionScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    NoInternetConnectionScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun NoInternetConnectionScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    NoInternetConnectionScreen()
}