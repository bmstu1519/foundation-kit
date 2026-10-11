package org.bmstu1519.foundation.ui.loader

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun PlatformLoader(
    modifier: Modifier,
    size: LoaderSize,
    color: Color?
) {
    val indicatorSize = when (size) {
        LoaderSize.Small -> 20.dp
        LoaderSize.Medium -> 32.dp
        LoaderSize.Large -> 48.dp
    }
    val strokeWidth = when (size) {
        LoaderSize.Small -> 2.dp
        LoaderSize.Medium -> 3.dp
        LoaderSize.Large -> 4.dp
    }
    CircularProgressIndicator(
        modifier = modifier.size(indicatorSize),
        strokeWidth = strokeWidth,
        color = color ?: MaterialTheme.colorScheme.primary
    )
}
