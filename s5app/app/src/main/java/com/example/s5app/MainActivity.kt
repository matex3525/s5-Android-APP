package com.example.s5app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.s5app.extension.toBitmap
import com.example.s5app.navigation.AlbumImageDetailsScreen
import com.example.s5app.navigation.AlbumScreen
import com.example.s5app.navigation.MainScreen
import com.example.s5app.screen.AlbumImageDetailsScreen
import com.example.s5app.screen.AlbumScreen
import com.example.s5app.screen.MainScreen
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.viewmodel.ConnectivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainActivityScreen(startDestination: Any) {
    val navController = rememberNavController()
    // Tworzymy stan dla SnackbarHost
    val snackbarHostState = remember { SnackbarHostState() }
    // Tworzymy coroutineScope do obsługi wywołań funkcji wyświetlającej Snackbar
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel= hiltViewModel<ConnectivityViewModel>()
    val isInternetAvailable by viewModel.isInternetAvailable.collectAsState(initial = true)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Cupid") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = contentColorFor(MaterialTheme.colorScheme.surface)
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primary, // Ustawiamy niestandardowy kolor tła
                    contentColor = MaterialTheme.colorScheme.onPrimary  // Kontrastowy kolor tekstu
                )
            }
        }
    ) {
        // Obsługa braku połączenia z internetem
        LaunchedEffect(isInternetAvailable) {
            if (!isInternetAvailable) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No internet connection",
                        duration = SnackbarDuration.Indefinite
                    )
                }
            } else {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss() // Ukryj aktualnego Snackbara
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(it)
        ) {
            composable<MainScreen> {
                MainScreen(navController = navController)
            }
            composable<AlbumScreen> {
                val args = it.toRoute<AlbumScreen>()
                AlbumScreen(navController, args.userToken, args.eventName, args.adminToken)
            }
            composable<AlbumImageDetailsScreen> {
                val args = it.toRoute<AlbumImageDetailsScreen>()
                val uri = Uri.parse(args.imageByteArray)
                AlbumImageDetailsScreen(uri!!.toBitmap(context)?.asImageBitmap()!!, args.userToken, args.imageId, args.adminToken)
            }
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S5appTheme {
                MainActivityScreen(MainScreen)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreviewLightMode() = S5appTheme(darkTheme = false) {
    MainActivityScreen(MainScreen)
}

@Preview(showBackground = true)
@Composable
fun MainPreviewDarkMode() = S5appTheme(darkTheme = true) {
    MainActivityScreen(MainScreen)
}