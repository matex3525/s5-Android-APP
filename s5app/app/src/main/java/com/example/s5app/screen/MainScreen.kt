package com.example.s5app.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.s5app.MainViewModel
import com.example.s5app.navigation.NewEventScreen
import com.example.s5app.navigation.EventMainPageScreen
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun MainScreen(navController: NavController? = null,model: MainViewModel = viewModel()) {
    var userTokenText by remember { mutableStateOf("") }
    var adminTokenText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var joinButtonEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.height(300.dp).fillMaxWidth(fraction = 0.9f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome to Cupid")
                Spacer(modifier = Modifier.size(width = 0.dp,height = 12.dp))

                TextField(
                    value = userTokenText,
                    onValueChange = { userTokenText = it },
                    label = { Text("Join code") }
                )

                Spacer(modifier = Modifier.height(4.dp))

                TextField(
                    value = adminTokenText,
                    onValueChange = { adminTokenText = it },
                    label = { Text("Admin token (optional)") }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    enabled = joinButtonEnabled,
                    onClick = {
                        joinButtonEnabled = false
                        model.enterEvent(userTokenText,adminTokenText,{
                            joinButtonEnabled = true
                            errorMessage = it
                        }) {
                            navController?.navigate(EventMainPageScreen)
                        }
                    }
                ) {
                    Text(text = "Join")
                }

                Text(text = "or")

                Button(
                    onClick = {
                        navController?.navigate(NewEventScreen)
                    }
                ) {
                    Text(text = "Create your first album")
                }
            }
        }
        Text(text = errorMessage)
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun MainScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    MainScreen(null)
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun MainScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    MainScreen(null)
}