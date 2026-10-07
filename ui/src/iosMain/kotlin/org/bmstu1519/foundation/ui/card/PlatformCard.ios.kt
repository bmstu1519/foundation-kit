package org.bmstu1519.foundation.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

private val IosCardShape = RoundedCornerShape(12.dp)

@Composable
actual fun PlatformCard(
    modifier: Modifier,
    onClick: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(IosCardShape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = IosCardShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(content = content)
    }
}
