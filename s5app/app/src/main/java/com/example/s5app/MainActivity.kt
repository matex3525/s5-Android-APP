package com.example.s5app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.s5app.navigation.AlbumScreen
import com.example.s5app.navigation.MainScreen
import com.example.s5app.screen.AlbumScreen
import com.example.s5app.screen.MainScreen
import com.example.s5app.ui.theme.S5appTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val pinkColor = Color(0xFFFFC0CB)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S5appTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Cupid") },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = pinkColor)
                        )
                    },
                    content = {
                        NavHost( navController = navController,
                            startDestination = MainScreen,
                            modifier = Modifier.padding(it) )
                        {
                            composable<MainScreen> {
                                MainScreen(PaddingValues(horizontal = 48.dp), navController)
                            }
                            composable<AlbumScreen> {
                                AlbumScreen()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var count by remember {
        mutableIntStateOf(0)
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "Hello",
            modifier = modifier,
        )
        Text(
            text = name + count,
            modifier = modifier,
        )
        Button(onClick = {
            count++
        }) {
            Text(count.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    S5appTheme {
        Greeting("Android")
    }
}