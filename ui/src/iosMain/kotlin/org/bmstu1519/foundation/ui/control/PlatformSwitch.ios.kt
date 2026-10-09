package org.bmstu1519.foundation.ui.control

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TrackWidth = 51.dp
private val TrackHeight = 31.dp
private val ThumbDiameter = 27.dp
private val ThumbPadding = 2.dp
private val IosSystemGreen = Color(0xFF34C759)
private val ThumbBorderColor = Color(0x14000000)

@Composable
actual fun PlatformSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant

    val trackColor by animateColorAsState(
        targetValue = if (checked) IosSystemGreen else inactiveTrackColor,
        animationSpec = spring()
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbDiameter - ThumbPadding else ThumbPadding,
        animationSpec = spring(stiffness = 500f)
    )

    Box(
        modifier = modifier
            .requiredSize(width = TrackWidth, height = TrackHeight)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(TrackHeight / 2))
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onCheckedChange != null
            ) {
                onCheckedChange?.invoke(!checked)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .requiredSize(ThumbDiameter)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .border(0.5.dp, ThumbBorderColor, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}
