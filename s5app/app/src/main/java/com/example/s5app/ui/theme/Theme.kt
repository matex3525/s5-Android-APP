package com.example.s5app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.s5app.MainActivityScreen
import com.example.s5app.navigation.MainScreen

/*
val pinkColor = Color(0xFFFFC0CB)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
*/

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFEB6C8A),
    onPrimary = Color.Black,
    secondary = Color(0xFFEF476F),
    onSecondary = Color.Black,
    background = Color.White,
    surface = Color(0xFFFFC0CB),
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF701429),
    onPrimary = Color.White,
    secondary = Color(0xFFEF476F),
    onSecondary = Color.White,
    background = Color.Black,
    surface = Color(0xFF420C19),
    onSurface = Color.White
)

@Composable
fun S5appTheme(darkTheme: Boolean = isSystemInDarkTheme(),content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme,
    typography = Typography,
    content = content
)

@Preview(showBackground = true)
@Composable
fun GreetingPreviewLightMode() = S5appTheme(darkTheme = false) {
    MainActivityScreen(MainScreen)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreviewDarkMode() = S5appTheme(darkTheme = true) {
    MainActivityScreen(MainScreen)
}