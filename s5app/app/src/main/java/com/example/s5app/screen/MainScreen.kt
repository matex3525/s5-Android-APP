package com.example.s5app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.s5app.dialog.CupidAlertDialog
import com.example.s5app.event.MainScreenEvent
import com.example.s5app.extension.dashedBorder
import com.example.s5app.model.AlbumTokens
import com.example.s5app.navigation.AlbumScreen
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CreateEventParams
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.viewmodel.MainScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(vm: MainScreenViewModel = hiltViewModel(), navController: NavController? = null) {
    var userTokenText by remember { mutableStateOf("") }
    var adminTokenText by remember { mutableStateOf("") }
    var eventNameText by remember { mutableStateOf("") }


    val showDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogText = remember { mutableStateOf("") }

    val coroutineScopeMainScreen = rememberCoroutineScope()

    val getEventResponse by vm.getEventResponse.collectAsStateWithLifecycle()
    val createEventResponse by vm.createEventResponse.collectAsStateWithLifecycle()
    val checkAdminTokenResponse by vm.checkAdminTokenResponse.collectAsStateWithLifecycle()

    val albumTokens by vm.albumTokensFlow.collectAsStateWithLifecycle()

    LaunchedEffect(getEventResponse) {
        if (getEventResponse is ApiResult.Success) {
            vm.addAlbumToken(AlbumTokens((getEventResponse as ApiResult.Success).data.eventName, userTokenText, null))
            navController?.navigate(AlbumScreen(userTokenText, (getEventResponse as ApiResult.Success).data.eventName, null))
            vm.onEvent(MainScreenEvent.ClearCurrentEventToken)
        } else if (getEventResponse is ApiResult.Error) {
            dialogTitle.value = "Error"
            dialogText.value = "Wrong user token, try again."
            showDialog.value = true
        }
    }

    LaunchedEffect(createEventResponse) {
        if (createEventResponse is ApiResult.Success) {
            vm.addAlbumToken(AlbumTokens(eventNameText, (createEventResponse as ApiResult.Success<CreateEventParams>).data.userToken, (createEventResponse as ApiResult.Success<CreateEventParams>).data.adminToken))
            navController?.navigate(AlbumScreen((createEventResponse as ApiResult.Success<CreateEventParams>).data.userToken, eventNameText, (createEventResponse as ApiResult.Success<CreateEventParams>).data.adminToken))
            vm.onEvent(MainScreenEvent.ClearPreviouslyCreatedEventName)
        } else if (createEventResponse is ApiResult.Error) {
            dialogTitle.value = "Error"
            dialogText.value = (createEventResponse as ApiResult.Error).message
            showDialog.value = true
        }
    }

    LaunchedEffect(checkAdminTokenResponse) {
        if (checkAdminTokenResponse is ApiResult.Success) {
            vm.addAlbumToken(AlbumTokens((checkAdminTokenResponse as ApiResult.Success).data.eventName, userTokenText, adminTokenText))
            navController?.navigate(AlbumScreen(userTokenText, (checkAdminTokenResponse as ApiResult.Success).data.eventName, adminTokenText))
            vm.onEvent(MainScreenEvent.ClearCurrentAdminToken)
        } else if (checkAdminTokenResponse is ApiResult.Error) {
            dialogTitle.value = "Error"
            dialogText.value = "Wrong admin or user token, try again."
            showDialog.value = true
            vm.onEvent(MainScreenEvent.ClearCurrentAdminToken)
        }
    }

    CupidAlertDialog(dialogTitle = dialogTitle.value, dialogText = dialogText.value, showDialog = showDialog.value) {
        showDialog.value = false // Zamknij dialog
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (albumTokens.isEmpty()) Arrangement.Center else Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(fraction = 0.9f)
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Welcome to Cupid")
                        Spacer(modifier = Modifier.size(width = 0.dp,height = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(0.5f),
                                    value = userTokenText,
                                    onValueChange = {
                                        if (it.length <= 50 && !it.contains("\n")) {
                                            userTokenText = it
                                        }
                                    },
                                    maxLines = 1,
                                    label = { Text("User token") }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    modifier = Modifier.fillMaxWidth(0.5f),
                                    value = adminTokenText,
                                    onValueChange = {
                                        if (it.length <= 6 && !it.contains("\n")) {
                                            adminTokenText = it
                                        }
                                    },
                                    maxLines = 1,
                                    label = { Text("Admin token") }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                onClick = {
                                    coroutineScopeMainScreen.launch {
                                        if (adminTokenText.isNotEmpty()) {
                                            vm.onEvent(MainScreenEvent.CheckAdminToken(userTokenText, adminTokenText))
                                        } else {
                                            vm.onEvent(MainScreenEvent.GetEvent(userTokenText))
                                        }
                                    }
                                }
                            ) {
                                Text(text = "Join")
                            }
                        }
                        Text(text = "or")
                        TextField(
                            value = eventNameText,
                            onValueChange = {
                                if (it.length <= 50 && !it.contains("\n")) {
                                    eventNameText = it
                                }
                            },
                            label = { Text("Event name")},
                            maxLines = 1)
                        if (albumTokens.isEmpty()) {
                            Button(
                                onClick = {
                                    coroutineScopeMainScreen.launch {
                                        vm.onEvent(MainScreenEvent.CreateEvent(eventNameText))
                                    }
                                }
                            ) {
                                Text(text = "Create your first album")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            Button(
                                onClick = {
                                    coroutineScopeMainScreen.launch {
                                        vm.onEvent(MainScreenEvent.CreateEvent(eventNameText))
                                    }
                                }
                            ) {
                                Text(text = "Create album")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        if (albumTokens.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                Row {
                    Text(
                        text = "Your Albums",
                        color = Color(0xFF701429),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(albumTokens) { item ->
                AlbumCell(navController, item)
            }
        }
    }
}

@Composable
fun AlbumCell(navController: NavController?, albumTokens: AlbumTokens) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(100.dp)
            .fillMaxWidth(0.5f)
            .dashedBorder(4.dp, MaterialTheme.colorScheme.secondary, 16.dp)
            .clickable {
                navController?.navigate(AlbumScreen(albumTokens.userToken, albumTokens.eventName, albumTokens.adminToken))
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = albumTokens.eventName,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
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