package org.bmstu1519.foundation.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformAlertDialog(
    alert: ActionableAlert,
    onDismissRequest: () -> Unit,
    modifier: Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                alert.submitButton.action()
                onDismissRequest()
            }) {
                alert.submitButton.buttonText?.let { Text(it) }
            }
        },
        dismissButton = alert.cancelButton?.let { cancelBtn ->
            {
                TextButton(onClick = {
                    cancelBtn.action()
                    onDismissRequest()
                }) {
                    cancelBtn.buttonText?.let { Text(it) }
                }
            }
        },
        title = {
            Text(text = alert.title)
        },
        text = alert.message?.let { msg ->
            {
                Text(text = msg)
            }
        }
    )
}
