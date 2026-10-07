# Foundation-Kit: План реализации

План создания кроссплатформенной модульной библиотеки (`core` + `ui`) для Kotlin Multiplatform и Compose Multiplatform=нативная реализация для каждой платформы.

---

## 1. Архитектура и структура

Отдельный Git-репозиторий: `/Users/me.gusta/mobileProjects/foundation-kit`

```text
foundation-kit/
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml      # Единые версии (Kotlin 2.x, Compose, Ktor, KVault)
├── build.gradle.kts            # Root конфиг
├── settings.gradle.kts         # include(":core", ":ui")
├── core/                       # KMP: без Compose, чистая логика/сервисы
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/         # expect контракты
│       ├── androidMain/        # Android actuals
│       ├── iosMain/            # iOS actuals
│       └── jvmMain/            # Desktop actuals
└── ui/                         # CMP: Compose Multiplatform, зависит от :core
    ├── build.gradle.kts
    └── src/
        ├── commonMain/         # expect Composable виджеты
        ├── androidMain/        # Material 3 реализации
        ├── iosMain/            # UIKitView / Cupertino реализации
        └── jvmMain/            # Desktop реализации
```

---

## 2. Разделение ответственности модулей

### Модуль `:core` (Чистый KMP)
- **Зависимости:** Kotlin Stdlib, Coroutines, Ktor Client Core, KVault, Koin (core). **Строго без Compose.**
- **MVP компоненты:**
  - `KVaultProvider` — безопасное хранилище (Android: EncryptedSharedPreferences / Keychain, iOS: Keychain).
  - `EngineProvider` — HTTP-клиент Ktor (Android: OkHttp / Android, iOS: Darwin, Desktop: CIO).
  - `HapticFeedbackProvider` — системный виброотклик (iOS: `UIImpactFeedbackGenerator`, Android: `Vibrator`).
  - `SessionManager<T>` / `AuthState<T>` — локальная авторизация и управление профилем:
    - Секретные данные (PIN, токены) в `KVaultProvider`.
    - Пользовательские настройки (имя, почта, конфиг `T`) через `kotlinx.serialization`.
    - Состояния: `Unauthorized`, `Locked` (PIN/Биометрия), `Authorized(T)`.
- **Roadmap (после MVP):**
  - `BiometricsProvider` — FaceID / TouchID / BiometricPrompt.
  - `ClipboardProvider` — работа с системным буфером обмена.
  - `PrivacyScreenProvider` — защита от скриншотов и скрытие в app switcher (`FLAG_SECURE`, blur). - не обязательно
  - `ShareProvider` — системный диалог "Поделиться" (`UIActivityViewController` / `Intent.ACTION_SEND`).

### Модуль `:ui` (Compose Multiplatform)
- **Зависимости:** `api(project(":core"))`, Compose Multiplatform Runtime + UI + Foundation.
- **MVP компоненты:**
  - `PlatformAlertDialog` — нативный алерт (Android: Material 3, iOS: `UIAlertController` через `UIKitView`).
  - `PlatformLoader` — индикатор загрузки (Android: `CircularProgressIndicator`, iOS: `UIActivityIndicatorView`).
  - `SystemAppearance` — системная тема, цвет статус-бара, светлая/темная тема.
- **Roadmap (после MVP):**
  - `PlatformBottomSheet` / `ActionSheet` (Android: `ModalBottomSheet`, iOS: `UISheetPresentationController`).
  - `PlatformSegmentedControl` (iOS: пилюля `UISegmentedControl`, Android: `TabRow`).
  - `PlatformSecureTextField` — защищенный ввод PIN/пароля (iOS: `UITextField.isSecureTextEntry`).
  - `PlatformPullToRefresh` — нативный bounce и обновление списка.
  - `PlatformWebView` — просмотрщик ссылок (Solscan, Terms).

---

## 3. Этапы реализации

### Этап 1. Инициализация репозитория и Gradle
- [ ] Создать директорию `foundation-kit`.
- [ ] Инициализировать Git.
- [ ] Настроить Gradle Wrapper (8.x).
- [ ] Создать `gradle/libs.versions.toml`:
  - `kotlin = "2.1.0"` (или совпадающая с MobileWallet)
  - `compose = "1.7.x"`
  - `ktor = "3.x"`
  - `kvault = "1.10.x"`
  - `kotlinx-serialization = "1.7.x"`
- [ ] Настроить корневой `build.gradle.kts` и `settings.gradle.kts`.

### Этап 2. Сборка модуля `:core`
- [ ] Сконфигурировать `core/build.gradle.kts` (таргеты: Android Library, iOS arm64/sim, JVM).
- [ ] Перенести и обобщить:
  - `EngineProvider` (`commonMain`, `androidMain`, `iosMain`, `jvmMain`).
  - `KVaultProvider` (`commonMain`, `androidMain`, `iosMain`).
  - `HapticFeedbackProvider` (`commonMain`, `androidMain`, `iosMain`).
  - `SessionManager<T>` и `AuthState<T>` (generic контракт и реализация на базе `KVaultProvider` + serialization).
- [ ] Проверить компиляцию: `./gradlew :core:assemble`.

### Этап 3. Сборка модуля `:ui`
- [ ] Сконфигурировать `ui/build.gradle.kts`:
  - Подключить Compose Multiplatform плагин.
  - Подключить `api(project(":core"))`.
- [ ] Реализовать:
  - `PlatformAlertDialog` (`expect/actual`).
  - `PlatformLoader` (`expect/actual`).
  - `SystemAppearance` (`expect/actual`).
- [ ] Проверить компиляцию: `./gradlew :ui:assemble`.

### Этап 4. Интеграция в `MobileWallet` через Composite Build
- [ ] В `MobileWallet/settings.gradle.kts` добавить:
  ```kotlin
  includeBuild("../foundation-kit")
  ```
- [ ] В `MobileWallet/composeApp/build.gradle.kts` подключить:
  ```kotlin
  commonMain.dependencies {
      implementation("com.bmstu1619.foundation:core")
      implementation("com.bmstu1619.foundation:ui")
  }
  ```
- [ ] Заменить локальные вызовы диалогов, лоадеров и KVault на вызовы из `foundation-kit`.
- [ ] Проверить сборку приложений:
  - Android: `./gradlew :composeApp:assembleDebug`
  - iOS Simulator: сборка через Xcode.

### Этап 5. Публикация и версионирование
- [ ] Подключить плагин `maven-publish` в `:core` и `:ui`.
- [ ] Настроить `group = "com.bmstu1619.foundation"`, `version = "0.1.0"`.
- [ ] Протестировать локальную публикацию: `./gradlew publishToMavenLocal`.
- [ ] Добавить инструкции по публикации в GitHub Packages / JitPack для релизов.
