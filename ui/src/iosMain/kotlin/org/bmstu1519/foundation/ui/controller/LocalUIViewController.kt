package org.bmstu1519.foundation.ui.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

/**
 * CompositionLocal for providing the host [UIViewController] to present native iOS modals and sheets.
 */
val LocalUIViewController = staticCompositionLocalOf<UIViewController?> { null }

/**
 * Resolves the active [UIViewController], prioritizing [LocalUIViewController] if provided,
 * and falling back to traversing [UIApplication.sharedApplication.keyWindow?.rootViewController].
 */
@Composable
fun rememberTopViewController(): UIViewController? {
    val local = LocalUIViewController.current
    if (local != null) {
        var top: UIViewController = local
        while (top.presentedViewController != null) {
            top = top.presentedViewController ?: break
        }
        return top
    }
    var current = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (current?.presentedViewController != null) {
        current = current.presentedViewController
    }
    return current
}
