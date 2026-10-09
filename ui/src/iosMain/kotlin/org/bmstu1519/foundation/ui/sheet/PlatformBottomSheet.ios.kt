package org.bmstu1519.foundation.ui.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import org.bmstu1519.foundation.ui.keyboard.rememberIosKeyboardHeight
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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

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
 * iOS реализация Apple HIG Sheet:
 * - Открывается сразу на максимум наверх (Large Detent), вплотную к статус-бару.
 * - Привязана намертво к нижнему краю экрана (Alignment.BottomCenter).
 * - Фон шторки заливает подбородок целиком до физического края стекла.
 * - Свайп вниз сворачивает в Medium (~52%) или закрывает.
 * - Круглая кнопка закрытия крестиком сверху справа (Apple Close Button).
 */
@Composable
actual fun PlatformBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    showCloseButton: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var currentDetent by remember { mutableStateOf(SheetDetent.Large) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val currentHeight = remember { Animatable(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(999f)
    ) {
        val screenHeight = maxHeight
        val screenHeightPx = with(density) { screenHeight.toPx() }

        // Максимально наверх: отступ ровно по статус-бару устройства (часы/островок)
        val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val topMargin = if (statusBarTop > 0.dp) statusBarTop else 44.dp
        val topMarginPx = with(density) { topMargin.toPx() }

        val largeHeightPx = (screenHeightPx - topMarginPx).coerceAtLeast(0f)
        val mediumHeightPx = screenHeightPx * 0.52f

        val dragThresholdPx = with(density) { 45.dp.toPx() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val keyboardHeight by rememberIosKeyboardHeight()
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val bottomInset = if (keyboardHeight > 0.dp) keyboardHeight else navBottom

        // Если клавиатура открывается, пока шторка в Medium - автоматически раскрываем во всю высоту (Large Detent)
        val isKeyboardOpen = keyboardHeight > 0.dp
        LaunchedEffect(isKeyboardOpen) {
            if (isKeyboardOpen && currentDetent == SheetDetent.Medium) {
                currentDetent = SheetDetent.Large
                currentHeight.animateTo(
                    largeHeightPx,
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                )
            }
        }

        val dismissWithAnimation: () -> Unit = {
            focusManager.clearFocus()
            keyboardController?.hide()
            scope.launch {
                isVisible = false
                currentHeight.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
                onDismissRequest()
            }
        }

        // Открываем сразу на максимальную высоту (Large Detent)
        LaunchedEffect(Unit) {
            isVisible = true
            currentHeight.snapTo(0f)
            currentHeight.animateTo(
                targetValue = largeHeightPx,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }

        // 1. Scrim (затемнение всего экрана от края до края)
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

        // 2. Sheet контейнер, намертво прикрепленный к низу экрана
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(with(density) { currentHeight.value.toDp() })
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
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
                            .pointerInput(mediumHeightPx, largeHeightPx) {
                                detectVerticalDragGestures(
                                    onDragEnd = {
                                        scope.launch {
                                            if (currentDetent == SheetDetent.Medium) {
                                                if (currentHeight.value > mediumHeightPx + dragThresholdPx) {
                                                    // Потянули вверх -> возвращаем во всю высоту (Large)
                                                    currentDetent = SheetDetent.Large
                                                    currentHeight.animateTo(
                                                        largeHeightPx,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessMediumLow
                                                        )
                                                    )
                                                } else if (currentHeight.value < mediumHeightPx - dragThresholdPx) {
                                                    // Потянули вниз -> закрываем
                                                    dismissWithAnimation()
                                                } else {
                                                    // Возвращаем в Medium
                                                    currentHeight.animateTo(
                                                        mediumHeightPx,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessMediumLow
                                                        )
                                                    )
                                                }
                                            } else {
                                                // Текущий detent: Large
                                                if (currentHeight.value < largeHeightPx - dragThresholdPx) {
                                                    if (currentHeight.value < mediumHeightPx - dragThresholdPx) {
                                                        // Сильно потянули вниз -> закрываем
                                                        dismissWithAnimation()
                                                    } else {
                                                        // Сворачиваем в Medium
                                                        currentDetent = SheetDetent.Medium
                                                        currentHeight.animateTo(
                                                            mediumHeightPx,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    // Возвращаем в Large
                                                    currentHeight.animateTo(
                                                        largeHeightPx,
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
                                            val target = if (currentDetent == SheetDetent.Large) largeHeightPx else mediumHeightPx
                                            currentHeight.animateTo(target, spring(stiffness = Spring.StiffnessMediumLow))
                                        }
                                    },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        scope.launch {
                                            val newHeight = (currentHeight.value - dragAmount).coerceIn(0f, largeHeightPx)
                                            currentHeight.snapTo(newHeight)
                                        }
                                    }
                                )
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // Тап по шторке переключает Large <-> Medium
                                scope.launch {
                                    if (currentDetent == SheetDetent.Medium) {
                                        currentDetent = SheetDetent.Large
                                        currentHeight.animateTo(
                                            largeHeightPx,
                                            spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        )
                                    } else {
                                        currentDetent = SheetDetent.Medium
                                        currentHeight.animateTo(
                                            mediumHeightPx,
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

                // Безопасный отступ под клавиатуру или Home Indicator (фон шторки уходит под край)
                Spacer(
                    modifier = Modifier.height(bottomInset)
                )
            }
        }
    }
}
