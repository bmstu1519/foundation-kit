package org.bmstu1519.foundation.ui.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSValue
import platform.UIKit.CGRectValue
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillChangeFrameNotification
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification

/**
 * Отслеживает точную высоту системной клавиатуры iOS через NSNotificationCenter:
 * - Учитывает подсказки/автокоррекцию (QuickType бар).
 * - Учитывает диктовку, эмодзи и любое изменение высоты.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
fun rememberIosKeyboardHeight(): State<Dp> {
    val keyboardHeight = remember { mutableStateOf(0.dp) }

    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter
        val mainQueue = NSOperationQueue.mainQueue

        val showObserver = notificationCenter.addObserverForName(
            name = UIKeyboardWillShowNotification,
            `object` = null,
            queue = mainQueue
        ) { notification ->
            val userInfo = notification?.userInfo
            val frameValue = userInfo?.get(UIKeyboardFrameEndUserInfoKey) as? NSValue
            val height = frameValue?.CGRectValue?.useContents {
                size.height.toFloat().dp
            }
            if (height != null) {
                keyboardHeight.value = height
            }
        }

        val changeObserver = notificationCenter.addObserverForName(
            name = UIKeyboardWillChangeFrameNotification,
            `object` = null,
            queue = mainQueue
        ) { notification ->
            val userInfo = notification?.userInfo
            val frameValue = userInfo?.get(UIKeyboardFrameEndUserInfoKey) as? NSValue
            val height = frameValue?.CGRectValue?.useContents {
                size.height.toFloat().dp
            }
            if (height != null) {
                keyboardHeight.value = height
            }
        }

        val hideObserver = notificationCenter.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = mainQueue
        ) { _ ->
            keyboardHeight.value = 0.dp
        }

        onDispose {
            notificationCenter.removeObserver(showObserver)
            notificationCenter.removeObserver(changeObserver)
            notificationCenter.removeObserver(hideObserver)
        }
    }

    return keyboardHeight
}
