package org.bmstu1519.foundation.ui.theme

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformTheme(
    isDark: Boolean,
    content: @Composable () -> Unit
)
