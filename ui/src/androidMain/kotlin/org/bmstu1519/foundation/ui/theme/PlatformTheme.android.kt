package org.bmstu1519.foundation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.bmstu1519.foundation.ui.system.SystemAppearance

@Composable
actual fun PlatformTheme(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    SystemAppearance(isDark = isDark)

    MaterialTheme(
        colorScheme = if (isDark) darkColorScheme() else lightColorScheme(),
        content = content
    )
}
