package com.example.s5app.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.s5app.screen.AlbumImage
import com.example.s5app.ui.theme.S5appTheme

@Composable
fun AlbumImageDetailsDialog(albumImage: AlbumImage, onDismissRequest: () -> Unit) {
//    var newComment by remember { mutableStateOf("") }
//    Dialog(onDismissRequest = { onDismissRequest() }) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(0.5f)
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(48.dp)
//                ) {
//                    Surface(
//                        shape = RoundedCornerShape(16.dp),
//                        modifier = Modifier
//
//                            .size(200.dp)
//
//                    ) {
//                        // Your content here
//                        albumImage.imageBitmap?.let {
//                            Image(
//                                bitmap = it,
//                                contentDescription = "Selected Image",
//                                modifier = Modifier.fillMaxSize(),
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    }
////                    Row(
////                        modifier = Modifier.fillMaxWidth(),
////                        verticalAlignment = Alignment.CenterVertically,
////                        horizontalArrangement = Arrangement.Center
////                    ) {
////                        TextField(
////                            modifier = Modifier.fillMaxWidth(0.4f),
////                            value = newComment,
////                            onValueChange = { newComment = it },
////                            singleLine = true,
////                            label = { Text("Add new comment") }
////                        )
////                        Spacer(modifier = Modifier.width(8.dp))
////                        Button(
////                            modifier = Modifier.fillMaxWidth(0.5f),
////                            onClick = {
////                                //@TODO: Join to an event by code.
////                                newComment = ""
////                            }
////                        ) {
////                            Text(text = "Add")
////                        }
////                    }
//                    Button(onClick = {
//
//                    }) {
//                        Text("Show comments")
//                    }
//                }
//            }
//        }
//    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AlbumImageDetailsDialogPreviewLightMode() = S5appTheme(darkTheme = false) {
//    AlbumImageDetailsDialog(albumImage = AlbumImage()) {
//
//    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AlbumImageDetailsDialogPreviewDarkMode() = S5appTheme(darkTheme = true) {
//    AlbumImageDetailsDialog(albumImage = AlbumImage()) {
//
//    }
}