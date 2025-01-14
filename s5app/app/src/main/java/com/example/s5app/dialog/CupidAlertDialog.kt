package com.example.s5app.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CupidAlertDialog(
    dialogTitle: String = "Cupid",
    dialogText: String = "Default text",
    showDialog: Boolean,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onConfirm() },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm() }
                ) {
                    Text("Ok")
                }
            }
        )
    }
}