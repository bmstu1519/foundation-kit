# Foundation-Kit: План реализации

План создания кроссплатформенной модульной библиотеки (`core` + `ui`) для Kotlin Multiplatform и Compose Multiplatform=нативная реализация для каждой платформы.

---

## sprint1: Выполненные задачи

- [x] **Реорганизация репозитория в модульную архитектуру**:
  - Настроен [settings.gradle.kts] с модулями `:core`, `:ui`, `:composeApp`.
  - Подключен плагин `maven-publish` для локальной публикации (`publishToMavenLocal`) и настроен `group = "org.bmstu1519.foundation"`, `version = "0.1.0"`.
- [x] **Модуль `:core` (чистый KMP, без Compose)**:
  - `EngineProvider`: кроссплатформенный клиент Ktor (Android: OkHttp, iOS: Darwin).
  - `KVaultProvider`: безопасное хранилище (Android: `EncryptedSharedPreferences` с потокобезопасной фабрикой, iOS: `Keychain`).
  - `HapticFeedbackProvider`: системный тактильный отклик (iOS: нативный Taptic Engine через `UIImpactFeedbackGenerator`, `UISelectionFeedbackGenerator`, `UINotificationFeedbackGenerator`; Android: `Vibrator` / `VibrationEffect`).
- [x] **Модуль `:ui` (Compose Multiplatform — все компоненты через `expect/actual`)**:
  - `PlatformTheme`: адаптивная тема (iOS: Apple System Colors, Primary `#007AFF`, `systemGroupedBackground`, `secondarySystemGroupedBackground`, статус-бар; Android: Material 3).
  - `PlatformSwitch`: нативный переключатель (iOS: фиксированные 51×31 pt, зелёный `#34C759`, белый диск с тенью и контуром, пружинная анимация; Android: Material 3 `Switch`).
  - `PlatformTextField`: поле ввода (iOS: Apple Inset стиль с адаптивным фоном `surfaceVariant`, скруглением 10 pt, без выреза в рамке; Android: M3 `OutlinedTextField`).
  - `PlatformButton`: кнопка (iOS: Apple HIG с радиусом 10 pt, высотой 44 pt, системным цветом `#007AFF`, `Primary`/`Secondary`/`Destructive`; Android: M3 `Button`/`OutlinedButton`).
  - `PlatformCard`: секция/карточка (iOS: Inset Grouped карточка с радиусом 12 pt, динамический `surface`: белая `#FFFFFF` в светлой теме, `#1C1C1E` в тёмной; Android: M3 `Card` 16 pt).
  - `PlatformAlertDialog`: нативный диалог (iOS: `UIAlertController` через `DisposableEffect` без лишних subview-артефактов, Android: Material 3 `AlertDialog`) + модели `ActionableAlert`, `ActionableButton`.
  - `PlatformBottomSheet`: модальный нижний экран (iOS: [Apple HIG Sheets](https://developer.apple.com/design/human-interface-guidelines/sheets) с detents: Medium ~50% и Large во всю высоту при потягивании за шторку вверх, круглой кнопкой закрытия крестиком сверху справа (30×30 pt), граббером 36×5 pt, Cupertino скруглением 16 pt, затемнением фона, отступами Home Indicator и жестом свайпа вниз; Android: Material 3 `ModalBottomSheet`).
  - `SystemAppearance`: динамическое управление цветом статус-бара (iOS: `setStatusBarStyle`, Android: `WindowInsetsControllerCompat`).
- [x] **Демо-экран и UX-полировка (`:composeApp`)**:
  - Реализован [`ShowcaseScreen`](file:///Users/me.gusta/mobileProjects/KmpTemplate/composeApp/src/commonMain/kotlin/org/bmstu1519/kmptemplate/ShowcaseScreen.kt) со всеми платформенными компонентами.
  - Полноэкранный скролл с отступом под Home Indicator (`WindowInsets.navigationBars`).
  - Корректная обработка клавиатуры (`.imePadding()`) — поле ввода и кнопки не перекрываются.
  - Закреплённая шапка под статус-баром (`WindowInsets.statusBars`) — контент не накладывается на часы/вырез при скролле.
  - Добавлена аннотация `@Preview` для отображения в Android Studio.
  - Настроена инициализация `initializeKVault` в [`MainActivity.kt`](file:///Users/me.gusta/mobileProjects/KmpTemplate/composeApp/src/androidMain/kotlin/org/bmstu1519/kmptemplate/MainActivity.kt).

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
│       ├── commonMain/         # EngineProvider, KVaultProvider
│       ├── androidMain/        # OkHttp, EncryptedSharedPreferences (KVault)
│       └── iosMain/            # Darwin, Keychain (KVault)
├── ui/                         # CMP: Compose Multiplatform, зависит от :core
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/         # PlatformTheme, PlatformSwitch, PlatformTextField,
│       │                       # PlatformButton, PlatformCard, PlatformAlertDialog,
│       │                       # PlatformBottomSheet, SystemAppearance
│       ├── androidMain/        # Material 3 реализации
│       └── iosMain/            # Apple HIG / Cupertino реализации
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
  - `SessionManager<T>` / `AuthState<T>` — локальная авторизация и управление профилем:
    - Секретные данные (PIN, токены) в `KVaultProvider`.
    - Пользовательские настройки (имя, почта, конфиг `T`) через `kotlinx.serialization`.
    - Состояния: `Unauthorized`, `Locked` (PIN/Биометрия), `Authorized(T)`.
  - `BiometricsProvider` — FaceID / TouchID / BiometricPrompt.
  - `ClipboardProvider` — работа с системным буфером обмена.
  - `PrivacyScreenProvider` — защита от скриншотов и скрытие в app switcher (`FLAG_SECURE`, blur).
  - `ShareProvider` — системный диалог "Поделиться" (`UIActivityViewController` / `Intent.ACTION_SEND`).

### Модуль `:ui` (Compose Multiplatform)
- **Зависимости:** `api(project(":core"))`, Compose Multiplatform Runtime + UI + Foundation, Material 3.
- **Реализованные компоненты (expect/actual):**
  - `PlatformTheme` — адаптивная тема под платформу (iOS: Apple System Colors, Android: Material 3).
  - `PlatformSwitch` — нативный переключатель (iOS: строгие 51×31 pt, зелёный `#34C759`, Android: Material 3 Switch).
  - `PlatformTextField` — нативное поле ввода (iOS: Apple Inset стиль без выреза в рамке, Android: OutlinedTextField).
  - `PlatformButton` — нативная кнопка (`Primary`, `Secondary`, `Destructive`).
  - `PlatformCard` — нативная карточка/секция (iOS: Inset Grouped, Android: Material 3 Card).
  - `PlatformAlertDialog` — нативный алерт (iOS: `UIAlertController` через `DisposableEffect`, Android: Material 3 `AlertDialog`).
  - `PlatformBottomSheet` — нативный модальный экран (iOS: Apple HIG Sheet с граббером, свайпом вниз и Cupertino скруглением, Android: Material 3 `ModalBottomSheet`).
  - `SystemAppearance` — системная тема, цвет статус-бара.
- **Roadmap (следующий спринт):**
  - `PlatformLoader` — индикатор загрузки (Android: `CircularProgressIndicator`, iOS: `UIActivityIndicatorView`).
  - `PlatformSegmentedControl` (iOS: пилюля `UISegmentedControl`, Android: `TabRow`).
  - `PlatformSecureTextField` — защищенный ввод PIN/пароля.
  - `PlatformPullToRefresh` — нативный bounce и обновление списка.
  - `PlatformWebView` — просмотрщик ссылок (Solscan, Terms).

---

## 3. Этапы реализации

### Этап 1. Инициализация репозитория и Gradle
- [x] Создать модули библиотеки в проекте (`:core`, `:ui`, `:composeApp`).
- [x] Настроить Gradle Wrapper и `gradle/libs.versions.toml`.
- [x] Настроить корневой `build.gradle.kts` и `settings.gradle.kts` с поддержкой `maven-publish`.

### Этап 2. Сборка модуля `:core`
- [x] Сконфигурировать `core/build.gradle.kts` (таргеты: Android Library, iOS arm64/sim).
- [x] Реализовать сервисы (`commonMain`, `androidMain`, `iosMain`):
  - `EngineProvider` (Ktor OkHttp / Darwin).
  - `KVaultProvider` (EncryptedSharedPreferences / Keychain).
  - `HapticFeedbackProvider` (Taptic Engine / Vibrator).
- [ ] Roadmap: `SessionManager<T>`, `AuthState<T>`, `BiometricsProvider`.
- [x] Проверить компиляцию: `./gradlew :core:assemble`.

### Этап 3. Сборка модуля `:ui`
- [x] Сконфигурировать `ui/build.gradle.kts` (`api(project(":core"))`, Compose Multiplatform).
- [x] Реализовать компоненты (`expect/actual` под iOS/Android):
  - `PlatformTheme` (адаптивная палитра под платформу)
  - `PlatformSwitch` (Apple HIG свитч 51×31 pt / M3 Switch)
  - `PlatformTextField` (Apple Inset стиль / M3 OutlinedTextField)
  - `PlatformButton` (Apple HIG 44 pt Primary/Secondary/Destructive / M3 Button)
  - `PlatformCard` (Apple Inset Grouped / M3 Card)
  - `PlatformAlertDialog` (нативный `UIAlertController` через DisposableEffect / M3 AlertDialog)
  - `PlatformBottomSheet` (Apple HIG Sheet с граббером и жестом свайпа / M3 ModalBottomSheet)
  - `SystemAppearance` (управление статус-баром)
- [ ] Roadmap: `PlatformLoader`, `PlatformSegmentedControl`, `PlatformPullToRefresh`.
- [x] Проверить компиляцию: `./gradlew :ui:assemble`.

### Интеграция в другие проекты через Composite Build
В `settings.gradle.kts` другого проекта:
```kotlin
includeBuild("/Users/me.gusta/mobileProjects/KmpTemplate")
```
В `build.gradle.kts`:
```kotlin
commonMain.dependencies {
    implementation("org.bmstu1519.foundation:core")
    implementation("org.bmstu1519.foundation:ui")
}
```

### Локальная публикация (Maven Local)
```bash
./gradlew publishToMavenLocal
```
В другом проекте добавить `mavenLocal()` в `repositories` и подключить:
```kotlin
implementation("org.bmstu1519.foundation:core:0.1.0")
implementation("org.bmstu1519.foundation:ui:0.1.0")
```
