package com.example.drakorapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object C {
    val BG       = Color(0xFF0D0D14)
    val Surface  = Color(0xFF14141E)
    val Card     = Color(0xFF1C1C28)
    val Border   = Color(0xFF2C2C3E)
    val Primary  = Color(0xFFE8365D)
    val Muted    = Color(0xFF8880A0)
    val SubText  = Color(0xFF5E5A74)
    val Gold     = Color(0xFFF5C842)
    val Green    = Color(0xFF3DD68C)
    val Blue     = Color(0xFF5B9BD5)
    val Orange   = Color(0xFFF5A742)
    val Red      = Color(0xFFFF4D6D)
}

@Composable
fun DrakorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary      = C.Primary,
            background   = C.BG,
            surface      = C.Surface,
            onPrimary    = Color.White,
            onBackground = Color.White,
            onSurface    = Color.White
        ),
        content = content
    )
}
