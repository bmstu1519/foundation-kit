package org.bmstu1519.foundation.ui.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Кроссплатформенный модальный нижний экран (Bottom Sheet).
 * - iOS: стиль Apple HIG Sheet с нативным граббером (drag indicator),
 *   жестом свайпа вниз для закрытия, detents (Medium / Large),
 *   круглой кнопкой закрытия крестиком сверху справа (Apple Close Button),
 *   Cupertino скруглением и отступами Home Indicator.
 * - Android: Material 3 ModalBottomSheet с поддержкой раскрытия и кнопки закрытия.
 */
@Composable
expect fun PlatformBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
)
