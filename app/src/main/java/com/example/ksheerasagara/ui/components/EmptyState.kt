package com.example.ksheerasagara.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.ui.theme.TextLight
import com.example.ksheerasagara.ui.theme.TextMedium

@Composable
fun EmptyState(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 52.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            title,
            fontSize   = 17.sp,
            fontWeight = FontWeight.Medium,
            color      = TextMedium,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle,
            fontSize  = 14.sp,
            color     = TextLight,
            textAlign = TextAlign.Center
        )
    }
}