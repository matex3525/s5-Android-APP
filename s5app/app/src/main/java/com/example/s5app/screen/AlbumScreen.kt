package com.example.s5app.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.s5app.extension.dashedBorder
import androidx.compose.foundation.layout.Column
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.s5app.R
import com.example.s5app.extension.getRawResourceUri
import com.example.s5app.extension.toUri
import com.example.s5app.navigation.AlbumImageDetailsScreen
import com.example.s5app.ui.theme.S5appTheme

class AlbumScreenViewModel : ViewModel() {
    val images = mutableStateListOf(AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage())
    fun addImage(imageBitmap: ImageBitmap? = null) {
        images.add(AlbumImage(null, imageBitmap))
    }
}

@Composable
fun AlbumScreen(vm: AlbumScreenViewModel = viewModel(), navController: NavController? = null, userToken: String, eventName: String) {
    var isUserTokenHidden by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Event name: $eventName",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isUserTokenHidden) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { isUserTokenHidden = false },
                ) {
                    Text("Show user token")
                }
            }
        } else {
            Row(modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "User token: $userToken")
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { isUserTokenHidden = true },
                ) {
                    Text("Hide user token")
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1.0f)
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    AddImageGridCell(vm)
                }
                items(vm.images) {item ->
                    AlbumImageGridCell(item, navController)
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { /* Do something */ },
            ) {
                Text("Export album")
            }
        }
    }
}

@Composable
fun AlbumImageGridCell(albumImage: AlbumImage? = null, navController: NavController? = null) {
    val context = LocalContext.current
//    val showDetailsDialog = remember { mutableStateOf(false) }
//    when {
//        showDetailsDialog.value -> {
////            if (albumImage != null) {
////                AlbumImageDetailsDialog(albumImage) {
////                    showDetailsDialog.value = false
////                }
////            }
//
//        }
//    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
            .clickable {
//                showDetailsDialog.value = true
                if (albumImage?.imageBitmap != null) {
                    val bitmapUri = albumImage.imageBitmap!!
                        .asAndroidBitmap()
                        .toUri(context)
                    navController?.navigate(AlbumImageDetailsScreen(imageByteArray = bitmapUri.toString()))
                } else {
                    val bitmapUri = getRawResourceUri(context, R.raw.noimg)
                    navController?.navigate(AlbumImageDetailsScreen(imageByteArray = bitmapUri.toString()))
                }
            }
    ) {
        // Your content here
        albumImage?.imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

data class AlbumImage(var id: Long? = null, var imageBitmap: ImageBitmap? = null)

@Composable
fun AddImageGridCell(vm: AlbumScreenViewModel) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
            .dashedBorder(4.dp, MaterialTheme.colorScheme.secondary, 16.dp)
            .clickable { expanded = true }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Add photo",
                //color = Color(0xFFEF476F)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            onClick = {
                expanded = false
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(cameraIntent)
            },
            text = {
                Text("Camera")
            }
        )
        DropdownMenuItem(
            onClick = {
                expanded = false
                galleryLauncher.launch("image/*")
            },
            text = {
                Text("Gallery")
            }
        )
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFFFFFFF)
@Composable
fun AlbumScreenPreviewLightMode() = S5appTheme(darkTheme = false) {
    AlbumScreen(viewModel(), null, "event_0", "Test event")
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun AlbumScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    AlbumScreen(viewModel(), null, "event_0", "Test event")
}