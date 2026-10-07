package org.bmstu1519.kmptemplate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.bmstu1519.foundation.core.haptic.getHapticFeedback
import org.bmstu1519.foundation.core.network.getEngine
import org.bmstu1519.foundation.core.storage.getKVaultInstance
import org.bmstu1519.foundation.ui.button.PlatformButton
import org.bmstu1519.foundation.ui.button.PlatformButtonVariant
import org.bmstu1519.foundation.ui.card.PlatformCard
import org.bmstu1519.foundation.ui.control.PlatformSwitch
import org.bmstu1519.foundation.ui.dialog.ActionableAlert
import org.bmstu1519.foundation.ui.dialog.ActionableButton
import org.bmstu1519.foundation.ui.dialog.PlatformAlertDialog
import org.bmstu1519.foundation.ui.input.PlatformTextField
import org.bmstu1519.foundation.ui.sheet.PlatformBottomSheet
import org.bmstu1519.foundation.ui.theme.PlatformTheme

@Composable
fun ShowcaseScreen() {
    var isDarkTheme by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertResultText by remember { mutableStateOf("Диалог ещё не открывался") }
    var showBottomSheet by remember { mutableStateOf(false) }

    // KVault state
    var vaultInputText by remember { mutableStateOf("") }
    var vaultStoredValue by remember {
        mutableStateOf(
            runCatching {
                getKVaultInstance().kVault.string("demo_key") ?: "(пусто)"
            }.getOrElse { "(хранилище не инициализировано в preview)" }
        )
    }

    // Engine state
    var engineStatusText by remember { mutableStateOf("Нажмите для проверки клиента") }

    PlatformTheme(isDark = isDarkTheme) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "Foundation Kit Showcase",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Демонстрация базовых модулей :core и :ui",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Секция 1: PlatformSwitch + PlatformTheme
                PlatformCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. SystemAppearance & Switch (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: UISwitch зеленый / status bar | Android: Material 3 Switch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isDarkTheme) "Тёмная тема (белый статус-бар)" else "Светлая тема (тёмный статус-бар)",
                                modifier = Modifier.weight(1f).padding(end = 12.dp)
                            )
                            PlatformSwitch(
                                checked = isDarkTheme,
                                onCheckedChange = {
                                    isDarkTheme = it
                                    getHapticFeedback().selection()
                                }
                            )
                        }
                    }
                }

                // Секция 2: PlatformAlertDialog + PlatformButton
                PlatformCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2. PlatformAlertDialog & Button (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: UIAlertController + кнопка с радиусом 10pt | Android: M3 Dialog + Button",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        PlatformButton(
                            onClick = { showAlert = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Показать нативный диалог")
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Результат: $alertResultText",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Секция 3: PlatformBottomSheet (Apple HIG Sheet / M3 ModalBottomSheet)
                PlatformCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. PlatformBottomSheet (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Apple HIG Sheet (граббер, свайп вниз, скругление) | Android: M3 ModalBottomSheet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        PlatformButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Открыть нативный Sheet")
                        }
                    }
                }

                // Секция 4: KVaultProvider + PlatformTextField
                PlatformCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("4. KVaultProvider & TextField", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Cupertino TextField без дырки в рамке | Android: OutlinedTextField",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        PlatformTextField(
                            value = vaultInputText,
                            onValueChange = { vaultInputText = it },
                            label = "Значение для сохранения",
                            placeholder = "Введите текст...",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PlatformButton(
                                onClick = {
                                    runCatching {
                                        val vault = getKVaultInstance().kVault
                                        vault.set("demo_key", vaultInputText)
                                        vaultStoredValue = vault.string("demo_key") ?: "(пусто)"
                                    }.onFailure {
                                        vaultStoredValue = "Ошибка: ${it.message}"
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Сохранить")
                            }
                            PlatformButton(
                                onClick = {
                                    runCatching {
                                        val vault = getKVaultInstance().kVault
                                        vault.deleteObject("demo_key")
                                        vaultStoredValue = "(пусто)"
                                    }.onFailure {
                                        vaultStoredValue = "Ошибка: ${it.message}"
                                    }
                                },
                                variant = PlatformButtonVariant.Secondary,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Очистить")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Сохранено в Vault: $vaultStoredValue",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Секция 5: EngineProvider
                PlatformCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("5. EngineProvider (:core)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Darwin Engine | Android: OkHttp Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        PlatformButton(
                            onClick = {
                                runCatching {
                                    val engine = getEngine()
                                    val client = engine.createHttpClient()
                                    engineStatusText = "HttpClient успешно создан (${engine::class.simpleName})"
                                    client.close()
                                }.onFailure {
                                    engineStatusText = "Ошибка: ${it.message}"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Инициализировать Ktor клиент")
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = engineStatusText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                Spacer(Modifier.height(16.dp))
            }
        }

        if (showAlert) {
            PlatformAlertDialog(
                alert = ActionableAlert(
                    title = "Подтверждение",
                    message = "Это нативный диалог платформы. Нажмите кнопку для проверки отклика.",
                    submitButton = ActionableButton(buttonText = "Принять") {
                        alertResultText = "Нажата кнопка 'Принять'"
                        showAlert = false
                    },
                    cancelButton = ActionableButton(buttonText = "Отмена") {
                        alertResultText = "Нажата кнопка 'Отмена'"
                        showAlert = false
                    }
                ),
                onDismissRequest = {
                    alertResultText = "Диалог закрыт (свайп/клик вне диалога)"
                    showAlert = false
                }
            )
        }

        if (showBottomSheet) {
            var sheetSwitchChecked by remember { mutableStateOf(true) }
            var sheetCommentText by remember { mutableStateOf("") }

            PlatformBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Apple HIG Sheet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "↕ Потяните за шторку вверх для открытия на всю высоту (Large Detent) или вниз для сворачивания (Medium Detent) и закрытия.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))

                    PlatformCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Параметры модального окна", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Включить виброотклик", modifier = Modifier.weight(1f))
                                PlatformSwitch(
                                    checked = sheetSwitchChecked,
                                    onCheckedChange = {
                                        sheetSwitchChecked = it
                                        getHapticFeedback().selection()
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    PlatformCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Тест тактильного отклика (:core)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Taptic Engine (iOS) / Vibrator (Android)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PlatformButton(
                                    onClick = { getHapticFeedback().lightImpact() },
                                    variant = PlatformButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Light")
                                }
                                PlatformButton(
                                    onClick = { getHapticFeedback().mediumImpact() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Medium")
                                }
                                PlatformButton(
                                    onClick = { getHapticFeedback().heavyImpact() },
                                    variant = PlatformButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Heavy")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PlatformButton(
                                    onClick = { getHapticFeedback().success() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Success")
                                }
                                PlatformButton(
                                    onClick = { getHapticFeedback().error() },
                                    variant = PlatformButtonVariant.Destructive,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Error")
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    PlatformCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Комментарий к действию", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            PlatformTextField(
                                value = sheetCommentText,
                                onValueChange = { sheetCommentText = it },
                                label = "Заметка",
                                placeholder = "Введите примечание...",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    PlatformButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Готово")
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ShowcaseScreenPreview() {
    ShowcaseScreen()
}
