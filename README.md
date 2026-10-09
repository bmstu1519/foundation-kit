# Foundation-Kit: План реализации

План создания кроссплатформенной модульной библиотеки (`core` + `ui`) для Kotlin Multiplatform и Compose Multiplatform с нативной реализацией для каждой платформы.

---

## sprint1: Выполненные задачи

- [x] **Реорганизация репозитория в модульную архитектуру**:
  - Настроен [settings.gradle.kts] с модулями `:core`, `:ui`, `:composeApp`.
  - Прямое подключение модулей через проектные зависимости (`project(":core")`, `project(":ui")`).
- [x] **Модуль `:core` (чистый KMP, без Compose)**:
  - `EngineProvider`: кроссплатформенный клиент Ktor (Android: OkHttp, iOS: Darwin).
  - `KVaultProvider`: безопасное хранилище (Android: `EncryptedSharedPreferences` с потокобезопасной фабрикой, iOS: `Keychain`).
  - `HapticFeedbackProvider`: системный тактильный отклик (iOS: нативный Taptic Engine через `UIImpactFeedbackGenerator`, `UISelectionFeedbackGenerator`, `UINotificationFeedbackGenerator`; Android: `Vibrator` / `VibrationEffect`).
- [x] **Модуль `:ui` (Compose Multiplatform — все компоненты через `expect/actual`)**:
  - `PlatformTheme`: адаптивная тема (iOS: Apple System Colors, Primary `#007AFF`, `systemGroupedBackground`, `secondarySystemGroupedBackground`, статус-бар; Android: Material 3).
  - `PlatformSwitch`: нативный переключатель (iOS: фиксированные 51×31 pt, зелёный `#34C759`, белый диск с тенью и контуром, пружинная анимация; Android: Material 3 `Switch`).
  - `PlatformTextField`: поле ввода (iOS: Apple Inset стиль с адаптивным фоном `surfaceVariant`, скруглением 10 pt, без выреза в рамке; встроен `BringIntoViewRequester` для автоскролла над клавиатурой; Android: M3 `OutlinedTextField`).
  - `PlatformButton`: кнопка (iOS: Apple HIG с радиусом 10 pt, высотой 44 pt, системным цветом `#007AFF`, оптимизированным паддингом 8 dp без переноса текста, `Primary`/`Secondary`/`Destructive`; Android: M3 `Button`/`OutlinedButton`).
  - `PlatformCard`: секция/карточка (iOS: Inset Grouped карточка с радиусом 12 pt, динамический `surface`: белая `#FFFFFF` в светлой теме, `#1C1C1E` в тёмной; Android: M3 `Card` 16 pt).
  - `PlatformAlertDialog`: нативный диалог (iOS: `UIAlertController` через `DisposableEffect` без лишних subview-артефактов, Android: Material 3 `AlertDialog`) + модели `ActionableAlert`, `ActionableButton`.
  - `PlatformBottomSheet`: модальный нижний экран по [Apple HIG Sheets](https://developer.apple.com/design/human-interface-guidelines/sheets):
    - Открывается сразу на максимум наверх (Large Detent, вплотную к статус-бару с учётом чёлки/островка).
    - Жесты: свайп вниз сворачивает в Medium (~52%) или закрывает; свайп вверх возвращает в Large; тап по грабберу переключает Large ↔ Medium.
    - Круглая Apple Close Button 30×30 pt с вектором `✕` сверху справа (`showCloseButton = true`).
    - Дно шторки привязано к нижнему краю экрана (0 pt, без серых зазоров под Home Indicator).
    - **Нативная обработка клавиатуры (iOS):** отслеживание высоты через `NSNotificationCenter` (`UIKeyboardWillShowNotification` + `CGRectValue`), точный учёт панели подсказок (QuickType: «о, он, ой»).
    - **Сброс фокуса и скрытие клавиатуры:** тап вне поля ввода (по фону/карточкам), начало скролла (`keyboardDismissMode = .onDrag`) или свайп шторки вниз автоматически гасят клавиатуру и сбрасывают фокус (`LocalFocusManager.clearFocus()`).
    - Android: Material 3 `ModalBottomSheet` с `skipPartiallyExpanded = true`.
  - `SystemAppearance`: динамическое управление цветом статус-бара (iOS: `setStatusBarStyle`, Android: `WindowInsetsControllerCompat`).
- [x] **Демо-экран и интеграция (`:composeApp`)**:
  - Реализован [`ShowcaseScreen`](file:///Users/me.gusta/mobileProjects/KmpTemplate/composeApp/src/commonMain/kotlin/org/bmstu1519/kmptemplate/ShowcaseScreen.kt) со всеми платформенными компонентами и тестом тактильного отклика (Light, Medium, Heavy, Success, Error).
  - В [`MainViewController.kt`](file:///Users/me.gusta/mobileProjects/KmpTemplate/composeApp/src/iosMain/kotlin/org/bmstu1519/kmptemplate/MainViewController.kt) настроен `onFocusBehavior = OnFocusBehavior.DoNothing` для бесшовной работы инсетов и скролла клавиатуры.
  - Полноэкранный скролл с отступом под Home Indicator (`WindowInsets.navigationBars`).
  - Закреплённая шапка под статус-баром (`WindowInsets.statusBars`).
  - Добавлена аннотация `@Preview` для отображения в Android Studio.
  - Настроена инициализация `initializeKVault` в [`MainActivity.kt`].

---

## 1. Архитектура и структура

Отдельные модули библиотеки в репозитории:

```text
KmpTemplate/
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml      # Единые версии (Kotlin 2.x, Compose, Ktor, KVault)
├── build.gradle.kts            # Root конфиг
├── settings.gradle.kts         # include(":core", ":ui", ":composeApp")
├── core/                       # KMP: без Compose, чистая логика/сервисы
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/         # EngineProvider, KVaultProvider, HapticFeedbackProvider
│       ├── androidMain/        # OkHttp, EncryptedSharedPreferences (KVault), Vibrator
│       └── iosMain/            # Darwin, Keychain (KVault), Taptic Engine
├── ui/                         # CMP: Compose Multiplatform, зависит от :core
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/         # PlatformTheme, PlatformSwitch, PlatformTextField,
│       │                       # PlatformButton, PlatformCard, PlatformAlertDialog,
│       │                       # PlatformBottomSheet, SystemAppearance
│       ├── androidMain/        # Material 3 реализации
│       └── iosMain/            # Apple HIG / Cupertino реализации, KeyboardHeight
└── composeApp/                 # Пример использования (sample app)
```

---

## 2. Разделение ответственности модулей

### Модуль `:core` (Чистый KMP)
- **Зависимости:** Kotlin Stdlib, Coroutines, Ktor Client Core, KVault, Koin (core). **Строго без Compose.**
- **MVP компоненты:**
  - `KVaultProvider` — безопасное хранилище (Android: EncryptedSharedPreferences, iOS: Keychain).
  - `EngineProvider` — HTTP-клиент Ktor (Android: OkHttp, iOS: Darwin).
  - `HapticFeedbackProvider` — тактильный отклик (iOS: Taptic Engine, Android: Vibrator).
- **Roadmap (после MVP):**
  - `SessionManager<T>` / `AuthState<T>` — локальная авторизация и управление профилем.
  - `BiometricsProvider` — FaceID / TouchID / BiometricPrompt.
  - `ClipboardProvider` — работа с системным буфером обмена.
  - `PrivacyScreenProvider` — защита от скриншотов и скрытие в app switcher (`FLAG_SECURE`, blur).
  - `ShareProvider` — системный диалог "Поделиться" (`UIActivityViewController` / `Intent.ACTION_SEND`).

### Модуль `:ui` (Compose Multiplatform)
- **Зависимости:** `api(project(":core"))`, Compose Multiplatform Runtime + UI + Foundation, Material 3.
- **Реализованные компоненты (expect/actual):**
  - `PlatformTheme` — адаптивная тема под платформу (iOS: Apple System Colors, Android: Material 3).
  - `PlatformSwitch` — нативный переключатель (iOS: строгие 51×31 pt, зелёный `#34C759`, Android: Material 3 Switch).
  - `PlatformTextField` — нативное поле ввода с автоскроллом над клавиатурой (`BringIntoViewRequester`).
  - `PlatformButton` — нативная кнопка (`Primary`, `Secondary`, `Destructive`) с оптимизированным паддингом 8 dp.
  - `PlatformCard` — нативная карточка/секция (iOS: Inset Grouped, Android: Material 3 Card).
  - `PlatformAlertDialog` — нативный алерт (iOS: `UIAlertController` через `DisposableEffect`, Android: Material 3 `AlertDialog`).
  - `PlatformBottomSheet` — нативный модальный экран (iOS: Apple HIG Sheet во всю высоту с Large/Medium detents, крестиком 30×30 pt, нативным отслеживанием клавиатуры и подсказок QuickType, сбросом фокуса по тапу/скроллу).
  - `SystemAppearance` — системная тема, цвет статус-бара.
- **Roadmap (следующий спринт):**
  - `PlatformLoader` — индикатор загрузки (Android: `CircularProgressIndicator`, iOS: `UIActivityIndicatorView`).
  - `PlatformSegmentedControl` (iOS: пилюля `UISegmentedControl`, Android: `TabRow`).
  - `PlatformSecureTextField` — защищенный ввод PIN/пароля.
  - `PlatformPullToRefresh` — нативный bounce и обновление списка.
  - `PlatformWebView` — просмотрщик ссылок (Solscan, Terms).

---

## 3. Сборка и интеграция

### Прямое подключение модулей (Рекомендуется)
Модули `:core` и `:ui` подключены в проекте напрямую:
```kotlin
commonMain.dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
}
```

### Интеграция в сторонние проекты через Composite Build
В `settings.gradle.kts` другого проекта:
```kotlin
includeBuild("/path/to/KmpTemplate")
```
В `build.gradle.kts`:
```kotlin
commonMain.dependencies {
    implementation("KmpTemplate:core")
    implementation("KmpTemplate:ui")
}
```
