package com.example.s5app.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "Welcome to Cupid",
            modifier = modifier,
        )
        Text(
            text = "Create your first album",
            modifier = modifier,
        )
//        Text(
//            text = name + count,
//            modifier = Modifier,
//        )
        Button(onClick = {
            //count++
        }) {
            Text("Create")
        }
    }
}