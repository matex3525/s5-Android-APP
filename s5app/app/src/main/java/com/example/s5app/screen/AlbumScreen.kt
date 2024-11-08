package com.example.s5app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.s5app.extension.dashedBorder

@Composable
fun AlbumScreen() {
    Box(
        modifier = Modifier
            .background(Color(0xFFFFE5EC))
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                AddImageGridCell()
            }
            item {
                AlbumImageGridCell()
            }
            item {
                AlbumImageGridCell()
            }
            item {
                AlbumImageGridCell()
            }
        }
    }
}

@Composable
fun AlbumImageGridCell() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEF476F),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
    ) {
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//        }
    }
}

@Composable
fun AddImageGridCell() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
            .dashedBorder(4.dp, Color(0xFFEF476F), 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Add photo",
                color = Color(0xFFEF476F)
            )
        }
    }
}