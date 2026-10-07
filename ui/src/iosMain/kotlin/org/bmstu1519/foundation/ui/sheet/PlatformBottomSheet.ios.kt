package org.bmstu1519.foundation.ui.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class SheetDetent {
    Medium,
    Large
}

/**
 * iOS круглая кнопка закрытия со стилем Apple HIG (30×30 pt, затемненный круг с крестиком).
 */
@Composable
private fun AppleSheetCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        val crossColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        Canvas(modifier = Modifier.size(10.dp)) {
            val strokeWidth = 2.dp.toPx()
            drawLine(
                color = crossColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crossColor,
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * iOS реализация Apple HIG Sheet с поддержкой detents (Medium / Large) и кнопки закрытия:
 * - При открытии появляется на ~50% высоты (Medium Detent).
 * - При потягивании за шторку (grabber) вверх разворачивается во всю высоту (Large Detent).
 * - Круглая кнопка закрытия крестиком сверху справа (Apple Close Button).
 * - При потягивании вниз из Large возвращается в Medium.
 * - При потягивании вниз из Medium или тапе на затемнённый фон закрывается.
 */
@Composable
actual fun PlatformBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    showCloseButton: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var currentDetent by remember { mutableStateOf(SheetDetent.Medium) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val offsetY = remember { Animatable(2000f) }

    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            dismissOnClickOutside = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val screenHeightPx = with(density) { screenHeight.toPx() }
            val topMarginPx = with(density) { 54.dp.toPx() }
            val largeHeightPx = (screenHeightPx - topMarginPx).coerceAtLeast(0f)
            val mediumHeightPx = screenHeightPx * 0.52f

            val largeOffset = 0f
            val mediumOffset = (largeHeightPx - mediumHeightPx).coerceAtLeast(0f)
            val dismissOffset = largeHeightPx

            val dragThresholdUpPx = with(density) { 40.dp.toPx() }
            val dragThresholdDownPx = with(density) { 50.dp.toPx() }

            val dismissWithAnimation: () -> Unit = {
                scope.launch {
                    isVisible = false
                    offsetY.animateTo(
                        targetValue = dismissOffset,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                    onDismissRequest()
                }
            }

            LaunchedEffect(Unit) {
                isVisible = true
                offsetY.snapTo(dismissOffset)
                offsetY.animateTo(
                    targetValue = mediumOffset,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }

            // Scrim (затемнение фона)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)),
                exit = fadeOut(spring(stiffness = Spring.StiffnessMediumLow))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = dismissWithAnimation
                        )
                )
            }

            // Sheet контейнер
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { largeHeightPx.toDp() })
                    .offset {
                        IntOffset(0, (topMarginPx + offsetY.value).roundToInt())
                    }
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    // Зона шапки шторки (Apple HIG Grabber + кнопка закрытия крестиком)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        // Центральная область шторки с жестом перетягивания
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(mediumOffset, largeOffset) {
                                    detectVerticalDragGestures(
                                        onDragEnd = {
                                            scope.launch {
                                                if (currentDetent == SheetDetent.Medium) {
                                                    if (offsetY.value < mediumOffset - dragThresholdUpPx) {
                                                        // Потянули вверх -> открываем во всю высоту (Large)
                                                        currentDetent = SheetDetent.Large
                                                        offsetY.animateTo(
                                                            largeOffset,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    } else if (offsetY.value > mediumOffset + dragThresholdDownPx) {
                                                        // Потянули вниз -> закрываем
                                                        dismissWithAnimation()
                                                    } else {
                                                        // Возвращаем в Medium
                                                        offsetY.animateTo(
                                                            mediumOffset,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    // Текущий detent: Large
                                                    if (offsetY.value > largeOffset + dragThresholdDownPx) {
                                                        if (offsetY.value > mediumOffset + dragThresholdDownPx) {
                                                            // Сильно потянули вниз -> закрываем
                                                            dismissWithAnimation()
                                                        } else {
                                                            // Сворачиваем обратно в Medium
                                                            currentDetent = SheetDetent.Medium
                                                            offsetY.animateTo(
                                                                mediumOffset,
                                                                spring(
                                                                  dampingRatio = Spring.DampingRatioLowBouncy,
                                                                  stiffness = Spring.StiffnessMediumLow
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        // Возвращаем в Large
                                                        offsetY.animateTo(
                                                            largeOffset,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            scope.launch {
                                                val target = if (currentDetent == SheetDetent.Large) largeOffset else mediumOffset
                                                offsetY.animateTo(target, spring(stiffness = Spring.StiffnessMediumLow))
                                            }
                                        },
                                        onVerticalDrag = { change, dragAmount ->
                                            change.consume()
                                            scope.launch {
                                                val newOffset = (offsetY.value + dragAmount).coerceIn(-15f, dismissOffset)
                                                offsetY.snapTo(newOffset)
                                            }
                                        }
                                    )
                                }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    // Тап по шторке также переключает Medium <-> Large
                                    scope.launch {
                                        if (currentDetent == SheetDetent.Medium) {
                                            currentDetent = SheetDetent.Large
                                            offsetY.animateTo(
                                                largeOffset,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            )
                                        } else {
                                            currentDetent = SheetDetent.Medium
                                            offsetY.animateTo(
                                                mediumOffset,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            )
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                            )
                        }

                        // Кнопка закрытия крестиком сверху справа (Apple Close Button)
                        if (showCloseButton) {
                            AppleSheetCloseButton(
                                onClick = dismissWithAnimation,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            )
                        }
                    }

                    // Контент шторки
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        content()
                    }

                    // Безопасный отступ снизу под Home Indicator
                    Spacer(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    )
                }
            }
        }
    }
}
