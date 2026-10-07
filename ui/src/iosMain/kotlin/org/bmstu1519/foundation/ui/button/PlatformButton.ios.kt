package org.bmstu1519.foundation.ui.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val IosDestructiveRed = Color(0xFFFF3B30)
private val IosButtonShape = RoundedCornerShape(10.dp)

@Composable
actual fun PlatformButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    variant: PlatformButtonVariant,
    content: @Composable RowScope.() -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryBackground = MaterialTheme.colorScheme.surfaceVariant

    val buttonColors = when (variant) {
        PlatformButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = primaryColor,
            contentColor = Color.White
        )
        PlatformButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = secondaryBackground,
            contentColor = primaryColor
        )
        PlatformButtonVariant.Destructive -> ButtonDefaults.buttonColors(
            containerColor = IosDestructiveRed,
            contentColor = Color.White
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 44.dp),
        enabled = enabled,
        shape = IosButtonShape,
        colors = buttonColors,
        content = content
    )
}
