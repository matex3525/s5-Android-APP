package com.example.s5app.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
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
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.navigation.MainScreen
import com.example.s5app.navigation.EventMainPageScreen

@Composable
fun NewEventScreen(navController: NavController? = null,model: MainViewModel = viewModel()) {
    var eventNameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var createButtonEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.height(240.dp).fillMaxWidth(fraction = 0.9f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Create a new event")
                Spacer(modifier = Modifier.size(width = 0.dp,height = 12.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    value = eventNameText,
                    onValueChange = { eventNameText = it },
                    label = { Text("Event name") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = { Text("e-mail") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.weight(0.5f),
                        onClick = {
                            navController?.navigate(MainScreen)
                        }
                    ) {
                        Text(text = "Back")
                    }

                    Button(
                        modifier = Modifier.weight(0.5f),
                        enabled = createButtonEnabled,
                        onClick = {
                            createButtonEnabled = false
                            model.createEvent(eventNameText,{
                                createButtonEnabled = true
                                errorMessage = it
                            }) {
                                navController?.navigate(EventMainPageScreen)
                            }
                        }
                    ) {
                        Text(text = "Create")
                    }
                }
            }
        }
        Text(text = errorMessage)
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun NewEventScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    NewEventScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun NewEventScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    NewEventScreen()
}