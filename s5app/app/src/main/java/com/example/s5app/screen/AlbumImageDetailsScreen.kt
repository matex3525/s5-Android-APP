package com.example.s5app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun AlbumImageDetailsScreen(albumImage: AlbumImage = AlbumImage()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                // Your content here
                albumImage.imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Text("Photo added at:")
            Text("Comments")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

            }
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        TextField(
//                            modifier = Modifier.fillMaxWidth(0.4f),
//                            value = newComment,
//                            onValueChange = { newComment = it },
//                            singleLine = true,
//                            label = { Text("Add new comment") }
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Button(
//                            modifier = Modifier.fillMaxWidth(0.5f),
//                            onClick = {
//                                //@TODO: Join to an event by code.
//                                newComment = ""
//                            }
//                        ) {
//                            Text(text = "Add")
//                        }
//                    }
//            Button(onClick = {
//
//            }) {
//                Text("Show comments")
//            }
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun AlbumImageDetailsScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    AlbumImageDetailsScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun AlbumImageDetailsScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    AlbumImageDetailsScreen()
}