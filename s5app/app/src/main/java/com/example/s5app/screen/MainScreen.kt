package com.example.s5app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.s5app.navigation.AlbumScreen
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun MainScreen(navController: NavController? = null) {
    var joinCodeText by remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Transparent)
    ) {
        Surface(
            modifier = Modifier.height(200.dp).fillMaxWidth(fraction = 0.9f).align(Alignment.Center),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome to Cupid")
                Spacer(modifier = Modifier.size(width = 0.dp,height = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        value = joinCodeText,
                        onValueChange = { joinCodeText = it },
                        label = { Text("Join code") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onClick = {
                            //@TODO: Join to an event by code.
                        }
                    ) {
                        Text(text = "Join")
                    }
                }

                Text(text = "or")

                Button(
                    onClick = {
                        //@TODO: Create a new event.
                        navController?.navigate(AlbumScreen)
                    }
                ) {
                    Text(text = "Create your first album")
                }
            }
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun MainScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    MainScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun MainScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    MainScreen()
}