package org.bmstu1519.foundation.ui.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class PlatformButtonVariant {
    Primary,
    Secondary,
    Destructive
}

@Composable
expect fun PlatformButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: PlatformButtonVariant = PlatformButtonVariant.Primary,
    content: @Composable RowScope.() -> Unit
)
