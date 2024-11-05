package com.example.s5app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.s5app.Greeting
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun MainScreen(padding: PaddingValues = PaddingValues(horizontal = 48.dp)) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .background(Color.White) // Set the background color to black
            .padding(padding)

    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(Color(0xFFFFC0CB), shape = RoundedCornerShape(16.dp))
                .align(Alignment.Center)
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = "Welcome to Cupid")
                Spacer(modifier = Modifier.size(width = 0.dp, height = 48.dp))


//        Text(
//            text = name + count,
//            modifier = Modifier,
//        )
                Button(
                    onClick = {
                    //count++
                    },
                    colors = ButtonDefaults.buttonColors( containerColor = Color(0xFFEF476F), // Background color
                        contentColor = Color.White // Text color
                    )
                ) {
                    Text(text = "Create your first album")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    S5appTheme {
        MainScreen()
    }
}