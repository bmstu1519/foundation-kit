# Foundation-Kit: План реализации

План создания кроссплатформенной модульной библиотеки (`core` + `ui`) для Kotlin Multiplatform и Compose Multiplatform=нативная реализация для каждой платформы.

---

## sprint1: Выполненные задачи

- [x] **Реорганизация репозитория в модульную архитектуру**:
  - Настроен [settings.gradle.kts](file:///Users/me.gusta/mobileProjects/KmpTemplate/settings.gradle.kts) с модулями `:core`, `:ui`, `:composeApp`.
  - Подключен плагин `maven-publish` для локальной публикации (`publishToMavenLocal`) и настроен `group = "org.bmstu1519.foundation"`, `version = "0.1.0"`.
- [x] **Модуль `:core` (чистый KMP, без Compose)**:
  - `EngineProvider`: кроссплатформенный клиент Ktor (Android: OkHttp, iOS: Darwin).
  - `KVaultProvider`: безопасное хранилище (Android: `EncryptedSharedPreferences` с потокобезопасной фабрикой, iOS: `Keychain`).
- [x] **Модуль `:ui` (Compose Multiplatform)**:
  - `PlatformAlertDialog`: нативный диалог (iOS: `UIAlertController` через `UIKitView`, Android: Material 3 `AlertDialog`) + модели `ActionableAlert`, `ActionableButton`.
  - `SystemAppearance`: управление стилем статус-бара (iOS: `setStatusBarStyle`, Android: `WindowInsetsControllerCompat`).
- [x] **Демо-экран и интеграция (`:composeApp`)**:
  - Реализован [`ShowcaseScreen`](file:///Users/me.gusta/mobileProjects/KmpTemplate/composeApp/src/commonMain/kotlin/org/bmstu1519/kmptemplate/ShowcaseScreen.kt) со всеми 4 компонентами.
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
│       ├── commonMain/         # PlatformAlertDialog, SystemAppearance
│       ├── androidMain/        # Material 3 AlertDialog, WindowInsetsControllerCompat
│       └── iosMain/            # UIAlertController (UIKitView), setStatusBarStyle
└── composeApp/                 # Пример использования (sample app)
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
