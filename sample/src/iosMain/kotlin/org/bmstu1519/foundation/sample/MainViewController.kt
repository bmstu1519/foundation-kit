package org.bmstu1519.foundation.sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import org.bmstu1519.foundation.ui.controller.LocalUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    lateinit var controller: UIViewController
    controller = ComposeUIViewController(
        configure = {
            onFocusBehavior = OnFocusBehavior.DoNothing
        }
    ) {
        CompositionLocalProvider(LocalUIViewController provides controller) {
            App()
        }
    }
    return controller
}