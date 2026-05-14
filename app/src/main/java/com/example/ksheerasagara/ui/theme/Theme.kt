package com.example.ksheerasagara.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary       = GreenPrimary,
    onPrimary     = Color.White,
    secondary     = GreenLight,
    background    = BgCream,
    surface       = CardWhite,
    error         = RedAlert,
    onBackground  = TextDark,
    onSurface     = TextDark
)

val AppTypography = Typography(
    bodyLarge   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall   = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    titleLarge  = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    titleSmall  = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    labelLarge  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun KsheeraSagaraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = AppTypography,
        content     = content
    )
}