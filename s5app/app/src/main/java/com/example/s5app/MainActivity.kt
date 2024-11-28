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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.s5app.navigation.AlbumImageDetailsScreen
import com.example.s5app.navigation.AlbumScreen
import com.example.s5app.navigation.MainScreen
import com.example.s5app.screen.AlbumImage
import com.example.s5app.screen.AlbumImageDetailsScreen
import com.example.s5app.screen.AlbumScreen
import com.example.s5app.screen.MainScreen
import com.example.s5app.ui.theme.S5appTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainActivityScreen(startDestination: Any) {
    val navController = rememberNavController()
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
                MainScreen(navController)
            }
            composable<AlbumScreen> {
                AlbumScreen(viewModel(), navController)
            }
            composable<AlbumImageDetailsScreen> {
                AlbumImageDetailsScreen(AlbumImage())
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