package org.bmstu1519.foundation.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformAlertDialog(
    alert: ActionableAlert,
    onDismissRequest: () -> Unit,
    modifier: Modifier
) {
    UIKitView(
        modifier = modifier,
        factory = {
            var currentController = UIApplication.sharedApplication.keyWindow?.rootViewController
            while (currentController?.presentedViewController != null) {
                currentController = currentController.presentedViewController
            }

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
                    alertController.dismissViewControllerAnimated(flag = true, completion = null)
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
                        alertController.dismissViewControllerAnimated(flag = true, completion = null)
                        onDismissRequest()
                    }
                )
                alertController.addAction(cancelAction)
            }

            currentController?.presentViewController(alertController, animated = true, completion = null)

            alertController.view
        },
        update = {}
    )
}
