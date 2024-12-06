package com.example.s5app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation.NavController
import com.example.s5app.MainViewModel
import com.example.s5app.MainViewModelImage
import com.example.s5app.extension.dashedBorder
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.navigation.EventMainPageScreen

@Composable
fun EventImagesScreen(navController: NavController? = null,model: MainViewModel = viewModel()) {
    val images = remember { mutableStateListOf<MainViewModelImage>() }
    val downloadImages by remember {
        mutableStateOf(model.getImageCount({}) {
            model.downloadImages({}) { image,_ ->
                images.add(image)
            }
        })
    };

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        navController?.navigate(EventMainPageScreen)
                    }
                ) {
                    Text(text = "Main")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.align(Alignment.Start)
            ) {
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {
                        images.clear()
                        model.getImageCount({}) {
                            mutableStateOf(model.getImageCount({}) {
                                model.downloadImages({}) { image,_ ->
                                    images.add(image)
                                }
                            })
                        }
                    }
                ) {
                    Text(text = "Refresh")
                }

                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {}
                ) {
                    Text(text = "Add Photo")
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(0.9f),
                columns = GridCells.Fixed(3)
            ) {
                items(images) { item ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp).size(100.dp).dashedBorder(4.dp,MaterialTheme.colorScheme.secondary,16.dp)
                    ) {
                        Image(
                            bitmap = item.image,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun EventImagesScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    EventImagesScreen()
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun EventImagesScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    EventImagesScreen()
}

/*val context = LocalContext.current
val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val bitmap = result.data?.extras?.get("data") as Bitmap
        imageBitmap = bitmap
        vm.addImage(bitmap.asImageBitmap())
    }
}
val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri ->
    uri?.let {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        imageBitmap = bitmap
        vm.addImage(bitmap.asImageBitmap())
    }
}*/