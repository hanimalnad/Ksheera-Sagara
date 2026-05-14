package com.example.ksheerasagara.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.ui.theme.RedAlert

@Composable
fun DeleteConfirmDialog(
    title: String = "Delete Entry",
    message: String = "Are you sure you want to delete this entry? This cannot be undone.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Text(message, fontSize = 15.sp)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(containerColor = RedAlert)
            ) {
                Text("Delete", color = Color.White, fontSize = 15.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 15.sp)
            }
        }
    )
}