package org.bmstu1519.foundation.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ActionableButton(
    val buttonText: String? = null,
    val action: () -> Unit = {}
)

data class ActionableAlert(
    val title: String,
    val message: String? = null,
    val submitButton: ActionableButton,
    val cancelButton: ActionableButton? = null
) {
    constructor(
        text: String,
        submitButton: ActionableButton,
        cancelButton: ActionableButton? = null
    ) : this(title = text, message = null, submitButton = submitButton, cancelButton = cancelButton)

    val text: String get() = title
}

@Composable
expect fun PlatformAlertDialog(
    alert: ActionableAlert,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
)
