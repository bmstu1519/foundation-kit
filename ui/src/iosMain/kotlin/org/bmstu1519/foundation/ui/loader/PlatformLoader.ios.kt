package org.bmstu1519.foundation.ui.loader

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityIndicatorView
import platform.UIKit.UIActivityIndicatorViewStyleLarge
import platform.UIKit.UIActivityIndicatorViewStyleMedium
import platform.UIKit.UIColor
import platform.UIKit.systemGrayColor

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun PlatformLoader(
    modifier: Modifier,
    size: LoaderSize,
    color: Color?
) {
    val style = when (size) {
        LoaderSize.Large -> UIActivityIndicatorViewStyleLarge
        else -> UIActivityIndicatorViewStyleMedium
    }
    val dpSize = when (size) {
        LoaderSize.Small -> 20.dp
        LoaderSize.Medium -> 32.dp
        LoaderSize.Large -> 48.dp
    }
    UIKitView(
        factory = {
            UIActivityIndicatorView(activityIndicatorStyle = style).apply {
                startAnimating()
                hidesWhenStopped = true
                backgroundColor = UIColor.clearColor
            }
        },
        modifier = modifier.size(dpSize),
        update = { indicator ->
            indicator.activityIndicatorViewStyle = style
            indicator.backgroundColor = UIColor.clearColor
            if (color != null) {
                indicator.color = UIColor.colorWithRed(
                    red = color.red.toDouble(),
                    green = color.green.toDouble(),
                    blue = color.blue.toDouble(),
                    alpha = color.alpha.toDouble()
                )
            } else {
                indicator.color = UIColor.systemGrayColor
            }
            indicator.startAnimating()
        },
        properties = UIKitInteropProperties(
            interactionMode = null,
            isNativeAccessibilityEnabled = false,
            placedAsOverlay = true
        )
    )
}
