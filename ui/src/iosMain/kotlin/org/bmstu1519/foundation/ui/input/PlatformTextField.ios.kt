package org.bmstu1519.foundation.ui.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun PlatformTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    placeholder: String?,
    label: String?,
    enabled: Boolean,
    singleLine: Boolean
) {
    val inputBackground = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(inputBackground)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && !placeholder.isNullOrEmpty()) {
                Text(
                    text = placeholder,
                    color = labelColor,
                    style = TextStyle(fontSize = 16.sp)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = singleLine,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(primaryColor),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
