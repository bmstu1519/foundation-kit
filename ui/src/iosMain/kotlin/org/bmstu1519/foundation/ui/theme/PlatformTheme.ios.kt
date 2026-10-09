package org.bmstu1519.foundation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.bmstu1519.foundation.ui.system.SystemAppearance

private val IosPrimaryBlue = Color(0xFF007AFF)

private val IosDarkColorScheme = darkColorScheme(
    primary = IosPrimaryBlue,
    onPrimary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color.White,
    surface = Color(0xFF1C1C1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFF8E8E93)
)

private val IosLightColorScheme = lightColorScheme(
    primary = IosPrimaryBlue,
    onPrimary = Color.White,
    background = Color(0xFFF2F2F7),
    onBackground = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF6C6C70)
)

@Composable
actual fun PlatformTheme(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    SystemAppearance(isDark = isDark)

    MaterialTheme(
        colorScheme = if (isDark) IosDarkColorScheme else IosLightColorScheme,
        content = content
    )
}
