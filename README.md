# Foundation-Kit: План реализации

План создания кроссплатформенной модульной библиотеки (`core` + `ui`) для Kotlin Multiplatform и Compose Multiplatform с нативной реализацией для каждой платформы.

---

## sprint1-1.1: Задачи и архитектура

- [x] **Реорганизация репозитория в модульную архитектуру**:
  - Настроен [settings.gradle.kts] с модулями `:core`, `:ui`, `:sample`.
  - Прямое подключение модулей через проектные зависимости (`project(":core")`, `project(":ui")`).
- [x] **Модуль `:core` (чистый KMP, без Compose)**:
  - `EngineProvider`: кроссплатформенный клиент Ktor (Android: OkHttp, iOS: Darwin).
  - `KVaultProvider`: безопасное хранилище (Android: `EncryptedSharedPreferences` с потокобезопасной фабрикой, iOS: `Keychain`).
  - `HapticFeedbackProvider`: системный тактильный отклик (iOS: нативный Taptic Engine через `UIImpactFeedbackGenerator`, `UISelectionFeedbackGenerator`, `UINotificationFeedbackGenerator`; Android: `Vibrator` / `VibrationEffect`).
- [/] **Модуль `:ui` (Нативные оверлеи UIKit из Kotlin + Бренд-тема)**:
  - `FoundationTheme`: базовая бренд-тема на Compose (Material Theme Builder) для верстки экранов без имитации псевдо-Cupertino.
  - `LocalUIViewController`: `CompositionLocal` провайдер корневого `UIViewController` для безопасного показа модалок без поиска по `keyWindow`.
  - `PlatformBottomSheet`: нативная шторка Apple (`UISheetPresentationController` с detents `.medium()`, `.large()`, системным свайпом, блюром и хостингом Compose через `ComposeUIViewController` на iOS; `ModalBottomSheet` на Android).
  - `PlatformAlertDialog`: нативный системный диалог (`UIAlertController` + `UIAlertAction` на iOS; Material 3 `AlertDialog` на Android).
  - `PlatformLoader`: нативный индикатор загрузки (`UIActivityIndicatorView` через `UIKitView` на iOS; `CircularProgressIndicator` на Android).
  - `SystemAppearance`: динамическое управление цветом статус-бара (iOS: `setStatusBarStyle`, Android: `WindowInsetsControllerCompat`).
- [x] **Демо-витрина (`:sample`)**:
  - Интеграционный экран со всеми системными оверлеями и тестом тактильного отклика (Light, Medium, Heavy, Success, Error).
  - В `MainViewController.kt` настроен `LocalUIViewController` для системных модалок.

---

## 1. Архитектура и структура

Отдельные модули библиотеки в репозитории:

```text
foundation-kit/
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml      # Единые версии (Kotlin 2.x, Compose, Ktor, KVault)
├── build.gradle.kts            # Root конфиг
├── settings.gradle.kts         # include(":core", ":ui", ":sample")
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
└── sample/                     # Пример использования (sample app)
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

### Модуль `:ui` (Compose Multiplatform + Нативные UIKit Оверлеи)
- **Зависимости:** `api(project(":core"))`, Compose Multiplatform Runtime + UI + Foundation, Material 3.
- **Архитектурный принцип:**
  - Экраны и верстка пишутся на чистом Compose в рамках единой дизайн-системы (без псевдо-Cupertino имитаций).
  - Системные модальные окна и индикаторы на iOS реализуются **напрямую через 100% нативный UIKit из Kotlin (`platform.UIKit.*`)**.
- **Компоненты (expect/actual):**
  - `FoundationTheme` — дизайн-система на Material Theme Builder.
  - `LocalUIViewController` — провайдер родительского UIViewController для презентации модалок.
  - `PlatformBottomSheet` — нативный `UISheetPresentationController` на iOS (с detents, родной физикой и хостингом Compose) / `ModalBottomSheet` на Android.
  - `PlatformAlertDialog` — нативный `UIAlertController` на iOS / `AlertDialog` на Android.
  - `PlatformLoader` — нативный `UIActivityIndicatorView` через `UIKitView` на iOS / `CircularProgressIndicator` на Android.
  - `SystemAppearance` — системный статус-бар.
- **Roadmap (следующий спринт):**
  - `PlatformShareSheet` — системный диалог «Поделиться» (`UIActivityViewController`).
  - `PlatformSegmentedControl` — системные вкладки (`UISegmentedControl`).
  - `PlatformDatePicker` — нативный селектор даты (`UIDatePicker`).
  - `PlatformWebView` — браузерный просмотрщик ссылок (`WKWebView` / `SFSafariViewController`).

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
includeBuild("/path/to/foundation-kit")
```
В `build.gradle.kts`:
```kotlin
commonMain.dependencies {
    implementation("foundation-kit:core")
    implementation("foundation-kit:ui")
}
```
