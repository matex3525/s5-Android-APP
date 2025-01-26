package com.example.s5app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.s5app.event.AlbumImageDetailsScreenEvent
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.viewmodel.AlbumImageDetailsScreenViewModel
import com.example.s5app.viewmodel.AlbumScreenViewModel
import java.util.Date

@Composable
fun AlbumImageDetailsScreen(
    imageBitmap: ImageBitmap,
    userToken: String,
    imageId: String
) {
    var newComment by remember { mutableStateOf("") }
    val vm: AlbumImageDetailsScreenViewModel = hiltViewModel(
        creationCallback = { factory: AlbumImageDetailsScreenViewModel.AlbumImageDetailsScreenViewModelFactory ->
            factory.create(imageBitmap, userToken, imageId)
        }
    )
    val bitmapSource = vm.bitmapSource.value
    val comments = vm.comments.value
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        bitmap = bitmapSource,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
            item {
                Text("Comments")
            }
            item {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newComment,
                        onValueChange = { newComment = it },
                        singleLine = true,
                        label = { Text("Add new comment") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onClick = {
                            vm.onEvent(AlbumImageDetailsScreenEvent.PostComment(newComment))
                            newComment = ""
                        }
                    ) {
                        Text(text = "Add")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
            items(items = comments) { comment ->
                AlbumImageDetailsCommentCell(comment = comment.text)
            }
        }
    }
}

@Composable
fun AlbumImageDetailsCommentCell(comment: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        shape = RectangleShape
    ) {
        Text(comment, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun AlbumImageDetailsScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    //AlbumImageDetailsScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun AlbumImageDetailsScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    //AlbumImageDetailsScreen()
}