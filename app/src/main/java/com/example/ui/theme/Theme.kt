package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldenAmber,
    onPrimary = Color.Black,
    secondary = WarmBeige,
    onSecondary = Color.Black,
    tertiary = AmberLight,
    onTertiary = Color.Black,
    background = RichBlack,
    onBackground = TextLight,
    surface = DarkGrey,
    onSurface = TextLight,
    surfaceVariant = SurfaceGrey,
    onSurfaceVariant = TextLight,
    error = CrimsonRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE65100), // Rich brown orange
    onPrimary = Color.White,
    secondary = Color(0xFF5D4037),
    onSecondary = Color.White,
    tertiary = Color(0xFFBF360C),
    onTertiary = Color.White,
    background = Color(0xFFFCFBEB), // Soft warm cream paper
    onBackground = Color(0xFF1C1A17),
    surface = Color.White,
    onSurface = Color(0xFF1C1A17),
    surfaceVariant = Color(0xFFF3EFE0),
    onSurfaceVariant = Color(0xFF3E2723),
    error = Color(0xFFC62828)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We set this to false to enforce our custom themed AzChords appearance
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
