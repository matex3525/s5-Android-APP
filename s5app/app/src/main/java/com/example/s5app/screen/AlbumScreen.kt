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
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.s5app.R
import com.example.s5app.event.AlbumScreenEvent
import com.example.s5app.extension.getRawResourceUri
import com.example.s5app.extension.toUri
import com.example.s5app.model.AlbumImage
import com.example.s5app.navigation.AlbumImageDetailsScreen
import com.example.s5app.network.GetGivenEventPhotoParams
import com.example.s5app.network.GetGivenEventPhotosParams
import com.example.s5app.ui.theme.S5appTheme
import com.example.s5app.util.BitmapUtil.base64ARGBToBitmap
import com.example.s5app.util.PDFUtil
import com.example.s5app.viewmodel.AlbumScreenViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Base64


@Composable
fun AlbumScreen(navController: NavController? = null, userToken: String, eventName: String, adminToken: String?) {
    val vm: AlbumScreenViewModel = hiltViewModel(
        creationCallback = { factory: AlbumScreenViewModel.AlbumScreenViewModelFactory ->
            factory.create(userToken)
        }
    )
    val context = LocalContext.current
    var isUserTokenHidden by remember { mutableStateOf(true) }
    var isAdminTokenHidden by remember { mutableStateOf(true) }
    val images = vm.images.value

    val getPDFResponse = vm.getPDFResponse.collectAsStateWithLifecycle()

    LaunchedEffect(getPDFResponse) {
        getPDFResponse.value?.let {
            val file = PDFUtil.savePDFFile(context, it, "event_photos")
            file?.let {
                PDFUtil.sharePdfFile(context, it)
            }
        }
    }

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
        if (adminToken != null) {
            if (isAdminTokenHidden) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { isAdminTokenHidden = false },
                    ) {
                        Text("Show admin token")
                    }
                }
            } else {
                Row(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Admin token: $adminToken")
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { isAdminTokenHidden = true },
                    ) {
                        Text("Hide admin token")
                    }
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
                    AddImageGridCell(vm, userToken)
                }
                items(images) {item ->
                    AlbumImageGridCell(item, navController, userToken, adminToken)
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    vm.onEvent(AlbumScreenEvent.GetPDFFileForEvent(userToken))
                },
            ) {
                Text("Export album")
            }
        }
    }
}

@Composable
fun AlbumImageGridCell(albumImage: AlbumImage, navController: NavController? = null, userToken: String, adminToken: String? = null) {
    val context = LocalContext.current

    val bitmap = base64ARGBToBitmap(albumImage.pixels)
    val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(100.dp)
            .clickable {
                val bitmapUri = imageBitmap
                    .asAndroidBitmap()
                    .toUri(context)
                navController?.navigate(
                    AlbumImageDetailsScreen(
                        bitmapUri.toString(),
                        userToken,
                        albumImage.imageId,
                        adminToken
                    )
                )
            }
    ) {
        // Your content here
//        albumImage?.imageBitmap?.let {
//            Image(
//                bitmap = it,
//                contentDescription = "Selected Image",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
        Image(
            bitmap = imageBitmap,
            contentDescription = albumImage.description,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}



@Composable
fun AddImageGridCell(vm: AlbumScreenViewModel, userToken: String) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) {
            return@rememberLauncherForActivityResult
        }
        val source = ImageDecoder.createSource(context.contentResolver,imageUri)
        val decodedListener = ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.isMutableRequired = true
        }
        val bitmap = ImageDecoder.decodeBitmap(source,decodedListener).asImageBitmap()
        vm.onEvent(AlbumScreenEvent.AddPhotoToEvent(userToken, bitmap))
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val decodedListener = ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
            val bitmap = ImageDecoder.decodeBitmap(source, decodedListener).asImageBitmap()
            vm.onEvent(AlbumScreenEvent.AddPhotoToEvent(userToken, bitmap))
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
                val imageFile = File.createTempFile("PNG",".png",context.cacheDir)
                imageFile.deleteOnExit()
                imageUri = FileProvider.getUriForFile(context,context.packageName + ".provider",imageFile)
                cameraLauncher.launch(imageUri)
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
    AlbumScreen(null, "event_0", "Test event", "aaaa")
}

@Preview(showBackground = true,backgroundColor = 0xFF000000)
@Composable
fun AlbumScreenPreviewDarkMode() = S5appTheme(darkTheme = true) {
    AlbumScreen(null, "event_0", "Test event", "uiuu")
}