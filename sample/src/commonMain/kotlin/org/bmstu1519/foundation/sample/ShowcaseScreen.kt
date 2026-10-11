package org.bmstu1519.foundation.sample

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import org.bmstu1519.foundation.ui.dialog.ActionableAlert
import org.bmstu1519.foundation.ui.dialog.ActionableButton
import org.bmstu1519.foundation.ui.dialog.PlatformAlertDialog
import org.bmstu1519.foundation.ui.loader.LoaderSize
import org.bmstu1519.foundation.ui.loader.PlatformLoader
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
                            text = "100% нативные UIKit оверлеи + Compose бренд-UI",
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

                // Секция 1: Тема и статус-бар
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. SystemAppearance & Тема", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Динамическое управление статус-баром iOS/Android",
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
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = {
                                    isDarkTheme = it
                                    getHapticFeedback().selection()
                                }
                            )
                        }
                    }
                }

                // Секция 2: Нативный PlatformLoader (UIActivityIndicatorView на iOS)
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2. Нативный PlatformLoader (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: UIActivityIndicatorView (UIKit) | Android: CircularProgressIndicator",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PlatformLoader(size = LoaderSize.Small)
                                Spacer(Modifier.height(4.dp))
                                Text("Small", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PlatformLoader(size = LoaderSize.Medium)
                                Spacer(Modifier.height(4.dp))
                                Text("Medium", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PlatformLoader(size = LoaderSize.Large)
                                Spacer(Modifier.height(4.dp))
                                Text("Large", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Секция 3: Нативный PlatformAlertDialog (UIAlertController на iOS)
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. Нативный PlatformAlertDialog (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: 100% UIAlertController через LocalUIViewController | Android: M3 AlertDialog",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showAlert = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Показать системный алерт")
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Результат: $alertResultText",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Секция 4: Нативный PlatformBottomSheet (UISheetPresentationController на iOS)
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("4. Нативный PlatformBottomSheet (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: UISheetPresentationController (medium/large, системный свайп и блюр) | Android: ModalBottomSheet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Открыть нативный BottomSheet")
                        }
                    }
                }

                // Секция 5: KVaultProvider
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("5. KVaultProvider (:core)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Keychain | Android: EncryptedSharedPreferences",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = vaultInputText,
                            onValueChange = { vaultInputText = it },
                            label = { Text("Значение для сохранения") },
                            placeholder = { Text("Введите текст...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
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
                            OutlinedButton(
                                onClick = {
                                    runCatching {
                                        val vault = getKVaultInstance().kVault
                                        vault.deleteObject("demo_key")
                                        vaultStoredValue = "(пусто)"
                                    }.onFailure {
                                        vaultStoredValue = "Ошибка: ${it.message}"
                                    }
                                },
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

                // Секция 6: EngineProvider
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("6. EngineProvider (:core)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Darwin Engine | Android: OkHttp Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
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

                // Секция 7: HapticFeedbackProvider
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("7. HapticFeedbackProvider (:core)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: Taptic Engine | Android: Vibrator",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { getHapticFeedback().lightImpact() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Light")
                            }
                            Button(
                                onClick = { getHapticFeedback().mediumImpact() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Medium")
                            }
                            OutlinedButton(
                                onClick = { getHapticFeedback().heavyImpact() },
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
                            Button(
                                onClick = { getHapticFeedback().success() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Success")
                            }
                            Button(
                                onClick = { getHapticFeedback().error() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Error")
                            }
                        }
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
                    message = "Это нативный системный диалог платформы. Нажмите кнопку для проверки отклика.",
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
                    alertResultText = "Диалог закрыт"
                    showAlert = false
                }
            )
        }

        if (showBottomSheet) {
            PlatformBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Нативный BottomSheet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "iOS: UISheetPresentationController с системной физикой Apple. Потяните вниз для сворачивания или закрытия.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(20.dp))

                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Лоадер внутри шторки", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                PlatformLoader(size = LoaderSize.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Закрыть")
                    }
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
