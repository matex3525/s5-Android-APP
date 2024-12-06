package com.example.s5app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.s5app.navigation.EventImagesScreen
import com.example.s5app.navigation.EventMainPageScreen
import com.example.s5app.navigation.MainScreen
import com.example.s5app.navigation.NewEventScreen
import com.example.s5app.screen.EventMainPageScreen
import com.example.s5app.screen.MainScreen
import com.example.s5app.screen.NewEventScreen
import com.example.s5app.screen.EventImagesScreen
import com.example.s5app.ui.theme.S5appTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainActivityScreen(startDestination: Any) {
    val navController = rememberNavController()
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "LocalViewModelStoreOwner.current == null" }
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
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(it)
        ) {
            composable<MainScreen> {
                MainScreen(navController,viewModel(viewModelStoreOwner))
            }
            composable<NewEventScreen> {
                NewEventScreen(navController,viewModel(viewModelStoreOwner))
            }
            /*composable<AlbumScreen> {
                AlbumScreen()
            }*/
            composable<EventMainPageScreen> {
                EventMainPageScreen(navController,viewModel(viewModelStoreOwner))
            }
            composable<EventImagesScreen> {
                EventImagesScreen(navController,viewModel(viewModelStoreOwner))
            }
        }
    }
}

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