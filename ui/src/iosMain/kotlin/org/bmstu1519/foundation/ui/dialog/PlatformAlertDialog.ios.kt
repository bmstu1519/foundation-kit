package org.bmstu1519.foundation.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import kotlinx.cinterop.ExperimentalForeignApi
import org.bmstu1519.foundation.ui.controller.rememberTopViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformAlertDialog(
    alert: ActionableAlert,
    onDismissRequest: () -> Unit,
    modifier: Modifier
) {
    val topViewController = rememberTopViewController()

    DisposableEffect(alert, topViewController) {
        if (topViewController == null) return@DisposableEffect onDispose {}

        val alertController = UIAlertController.alertControllerWithTitle(
            title = alert.title,
            message = alert.message,
            preferredStyle = UIAlertControllerStyleAlert
        )

        val submitAction = UIAlertAction.actionWithTitle(
            title = alert.submitButton.buttonText,
            style = UIAlertActionStyleDefault,
            handler = {
                alert.submitButton.action()
                onDismissRequest()
            }
        )
        alertController.addAction(submitAction)

        alert.cancelButton?.let { cancelBtn ->
            val cancelAction = UIAlertAction.actionWithTitle(
                title = cancelBtn.buttonText,
                style = UIAlertActionStyleCancel,
                handler = {
                    cancelBtn.action()
                    onDismissRequest()
                }
            )
            alertController.addAction(cancelAction)
        }

        topViewController.presentViewController(alertController, animated = true, completion = null)

        onDispose {
            if (alertController.presentingViewController != null) {
                alertController.dismissViewControllerAnimated(flag = true, completion = null)
            }
        }
    }
}
