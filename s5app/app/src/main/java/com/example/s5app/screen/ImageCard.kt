package com.example.s5app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Base64
import android.graphics.BitmapFactory
import com.example.s5app.model.AlbumImage

@Composable
fun ImageCard(image: AlbumImage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Decode the base64 string to a Bitmap
        val decodedString = Base64.getDecoder().decode(image.pixels)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

        // Display the image
        Image(
            bitmap = imageBitmap,
            contentDescription = image.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Crop
        )

        // Display the description
        BasicText(
            text = image.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}