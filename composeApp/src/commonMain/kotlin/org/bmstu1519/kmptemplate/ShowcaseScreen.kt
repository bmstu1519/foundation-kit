package org.bmstu1519.kmptemplate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.bmstu1519.foundation.core.network.getEngine
import org.bmstu1519.foundation.core.storage.getKVaultInstance
import org.bmstu1519.foundation.ui.dialog.ActionableAlert
import org.bmstu1519.foundation.ui.dialog.ActionableButton
import org.bmstu1519.foundation.ui.dialog.PlatformAlertDialog
import org.bmstu1519.foundation.ui.system.SystemAppearance

@Composable
fun ShowcaseScreen() {
    var isDarkTheme by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertResultText by remember { mutableStateOf("Диалог ещё не открывался") }

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

    SystemAppearance(isDark = isDarkTheme)

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Foundation Kit Showcase",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Демонстрация базовых модулей :core и :ui",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Секция 1: SystemAppearance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. SystemAppearance (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Управление status bar (светлые/тёмные иконки) и темой",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isDarkTheme) "Тёмная тема (белый статус-бар)" else "Светлая тема (тёмный статус-бар)")
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                        }
                    }
                }

                // Секция 2: PlatformAlertDialog
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2. PlatformAlertDialog (:ui)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "iOS: UIAlertController через UIKitView | Android: Material 3 AlertDialog",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
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

                // Секция 3: KVaultProvider
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. KVaultProvider (:core)", style = MaterialTheme.typography.titleMedium)
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
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

                // Секция 4: EngineProvider
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("4. EngineProvider (:core)", style = MaterialTheme.typography.titleMedium)
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
    }
}

@Preview
@Composable
fun ShowcaseScreenPreview() {
    ShowcaseScreen()
}
