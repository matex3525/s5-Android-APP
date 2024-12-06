package com.example.s5app.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.s5app.MainViewModel
import com.example.s5app.MainViewModelImage
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.navigation.MainScreen
import com.example.s5app.navigation.EventImagesScreen

@Composable
fun EventMainPageScreen(navController: NavController? = null,model: MainViewModel = viewModel()) {
    val userToken by remember { mutableStateOf(model.state.value.currentUserToken) }
    val adminToken by remember { mutableStateOf(model.state.value.currentAdminToken) }

    var eventName by remember { mutableStateOf("[default]") }
    val eventNameGet by remember { mutableStateOf(model.getCurrentEventTitle { eventName = it }) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        navController?.navigate(EventImagesScreen)
                    }
                ) {
                    Text(text = "Images")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).height(110.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Welcome to",fontSize = 24.sp)
                    Text(text = eventName,fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).height(110.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Join code",fontSize = 24.sp)
                    Text(text = userToken,fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if(adminToken.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.9f).height(110.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Admin code",fontSize = 24.sp)
                        Text(text = adminToken,fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).height(110.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Danger zone",fontSize = 24.sp)

                    Row {
                        Button(
                            onClick = {
                                model.leaveCurrentEvent()
                                navController?.navigate(MainScreen)
                            }
                        ) {
                            Text(text = "Leave event")
                        }

                        if(adminToken.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    model.deleteCurrentEvent({
                                        //@TODO: Display error message when deletion didn't succeed.
                                    }) {
                                        navController?.navigate(MainScreen)
                                    }
                                }
                            ) {
                                Text(text = "Delete event")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun EventMainPageScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    EventMainPageScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun EventMainPageScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    EventMainPageScreen()
}