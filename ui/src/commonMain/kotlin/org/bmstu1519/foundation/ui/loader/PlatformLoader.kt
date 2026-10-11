package org.bmstu1519.foundation.ui.loader

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class LoaderSize {
    Small,
    Medium,
    Large
}

/**
 * Кроссплатформенный системный индикатор загрузки (Loader / Activity Indicator).
 * - iOS: 100% нативный UIKit `UIActivityIndicatorView` через `UIKitView`.
 * - Android: Material 3 `CircularProgressIndicator`.
 */
@Composable
expect fun PlatformLoader(
    modifier: Modifier = Modifier,
    size: LoaderSize = LoaderSize.Medium,
    color: Color? = null
)
