package com.example.s5app.util
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

class HTMLCreatorUwU {

    fun createHtml(imageNames: List<String>): String {
        val htmlbuilder = StringBuilder()
        htmlbuilder.append(
            """
<!DOCTYPE html>
<html>
<head>
  <style>
p {
  height: 200px;
  width: 500px;
  margin: 100px auto;
  position: relative;
}

img {
  height: 150px;
  position: absolute;
  left: 0;
  offset-path: path('m 0 50 q 50-30 100-30 t 100 30 100 0 100-30 100 30');
  box-shadow: 1px 1px 3px #0008;
  transition: transform .4s ease-out, offset-path .4s cubic-bezier(.77,-1.17,.75,.84),box-shadow .3s, z-index .3s;
  z-index: 0;
}

img:hover {
  transform: scale(3);
  /* on hover, the path gets a bit shorter & flattened & shifted to left/bottom a bit for nicer movement */
  offset-path: path('m 5 65 q 45-0 90-0 t 90 0 90 0 90-0 90 0');
  box-shadow: 3px 4px 10px #0006;
  /* ensures that image gets on top of stack at the start of "popping" animation
     and gets back at the end of getting back. With smaller value, 2 different transitions would be needed */
  z-index: 999;
}

/* 3 images */
img:nth-last-child(3):first-child {
  offset-distance: 17%;
}
img:nth-last-child(2):nth-child(2) {
  offset-distance: 49%;
}
img:last-child:nth-child(3) {
  offset-distance: 81%;
}

/* 4 images */
img:nth-last-child(4):first-child {
  offset-distance: 10%;
}
img:nth-last-child(3):nth-child(2) {
  offset-distance: 35%;
}
img:nth-last-child(2):nth-child(3) {
  offset-distance: 65%;
}
img:last-child:nth-child(4) {
  offset-distance: 90%;
}

/* 5 images */
img:nth-last-child(5):first-child {
  offset-distance: 0%;
}
img:nth-last-child(4):nth-child(2) {
  offset-distance: 25%;
}
img:nth-last-child(3):nth-child(3) {
  offset-distance: 51%;
}
img:nth-last-child(2):nth-child(4) {
  offset-distance: 75%;
}
img:last-child:nth-child(5) {
  offset-distance: 100%;
}
</style>
</head>
<body>
<p>
            """.trimIndent()
        )
        imageNames.forEachIndexed { i, image_b64 ->
            htmlbuilder.append(
                """
                <img src="data:image/png;base64, $image_b64">
                """.trimIndent()
            )
        }

        htmlbuilder.append(
            """
        </p>
        </body>
        </html>
            """.trimIndent()
        )
        return htmlbuilder.toString()
    }

    fun saveAndOpenHtml(context: Context, htmlContent: String, fileName: String) {
        // Save the HTML content to a file
        val file = File(context.filesDir, fileName)
        file.writeText(htmlContent)

        // Create a URI for the file
        val uri = Uri.fromFile(file)

        // Create an intent to open the file in a web browser
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "text/html")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Start the activity to open the file
        context.startActivity(intent)
    }

}