package com.example.androidtv.ui.theme

import android.content.Context
import android.os.Vibrator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalVibrator = staticCompositionLocalOf<Vibrator> {
    error("No vibrator provided")
}

// Dark mode colors - default for Android TV setup
private val DarkColorScheme = darkColors(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AndroidTVTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {

    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    CompositionLocalProvider(
        LocalVibrator provides vibrator
    ) {
        MaterialTheme(
            colors = DarkColorScheme,
            typography = Typography(),  // Add custom typography
            shapes = Shapes,          // Define custom shapes
            content = content
        )
    }

}
