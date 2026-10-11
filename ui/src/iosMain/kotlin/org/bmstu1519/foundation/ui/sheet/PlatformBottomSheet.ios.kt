package org.bmstu1519.foundation.ui.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.bmstu1519.foundation.ui.controller.LocalUIViewController
import org.bmstu1519.foundation.ui.controller.rememberTopViewController
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIColor
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UIPresentationController
import platform.UIKit.UISheetPresentationControllerDetent
import platform.UIKit.presentationController
import platform.UIKit.sheetPresentationController
import platform.UIKit.systemBackgroundColor
import platform.darwin.NSObject

private class SheetDismissDelegate(
    private val onDismiss: () -> Unit
) : NSObject(), UIAdaptivePresentationControllerDelegateProtocol {
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss()
    }
}

@Composable
private fun IosSheetCloseButton(
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

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun PlatformBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    showCloseButton: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val topViewController = rememberTopViewController()
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    val currentContent by rememberUpdatedState(content)
    val currentColorScheme by rememberUpdatedState(MaterialTheme.colorScheme)
    val currentTypography by rememberUpdatedState(MaterialTheme.typography)
    val currentShapes by rememberUpdatedState(MaterialTheme.shapes)

    DisposableEffect(topViewController) {
        if (topViewController == null) return@DisposableEffect onDispose {}

        val dismissDelegate = SheetDismissDelegate {
            currentOnDismiss()
        }

        lateinit var sheetController: platform.UIKit.UIViewController
        sheetController = ComposeUIViewController {
            CompositionLocalProvider(LocalUIViewController provides sheetController) {
                MaterialTheme(
                    colorScheme = currentColorScheme,
                    typography = currentTypography,
                    shapes = currentShapes
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = currentColorScheme.surface
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 28.dp),
                                content = currentContent
                            )
                            if (showCloseButton) {
                                IosSheetCloseButton(
                                    onClick = { currentOnDismiss() },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 14.dp, end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        sheetController.view.backgroundColor = UIColor.systemBackgroundColor
        sheetController.modalPresentationStyle = UIModalPresentationPageSheet
        sheetController.sheetPresentationController?.apply {
            detents = listOf(
                UISheetPresentationControllerDetent.mediumDetent(),
                UISheetPresentationControllerDetent.largeDetent()
            )
            prefersGrabberVisible = true
            prefersScrollingExpandsWhenScrolledToEdge = true
        }

        sheetController.presentationController?.delegate = dismissDelegate

        topViewController.presentViewController(sheetController, animated = true, completion = null)

        onDispose {
            if (sheetController.presentingViewController != null) {
                sheetController.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }
}
