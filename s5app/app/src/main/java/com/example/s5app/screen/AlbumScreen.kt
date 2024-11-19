package com.example.s5app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class AlbumScreenViewModel(): ViewModel() {
    val images = mutableStateListOf(AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage())
    fun addImage(imageBitmap: ImageBitmap? = null) {
        images.add(AlbumImage(null, imageBitmap))
    }
}

@Composable
fun AlbumScreen(vm: AlbumScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .background(Color(0xFFFFE5EC))
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
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
                    AlbumImageGridCell(item)
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF476F), // Background color
                    contentColor = Color.White // Text color
                )
            ) {
                Text("Export album")
            }
        }
    }
}

@Composable
fun AlbumImageGridCell(albumImage: AlbumImage? = null) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
    ) {
        // Your content here
        albumImage?.imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize()
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
            .dashedBorder(4.dp, Color(0xFFEF476F), 16.dp)
            .clickable { expanded = true }
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