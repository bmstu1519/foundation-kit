package org.bmstu1519.foundation.core.haptic

/**
 * Типы системного тактильного отклика:
 * - LightImpact / MediumImpact / HeavyImpact — физический щелчок разной силы (нажатия кнопок, свайпы).
 * - Selection — легкий щелчок выбора (скролл пикера, переключение свитча).
 * - Success / Warning / Error — паттерны системных уведомлений (успех транзакции, неверный ввод, ошибка).
 */
enum class HapticFeedbackType {
    LightImpact,
    MediumImpact,
    HeavyImpact,
    Selection,
    Success,
    Warning,
    Error
}

interface HapticFeedbackProvider {
    /**
     * Воспроизвести системный тактильный отклик.
     */
    fun perform(type: HapticFeedbackType)

    fun lightImpact() = perform(HapticFeedbackType.LightImpact)
    fun mediumImpact() = perform(HapticFeedbackType.MediumImpact)
    fun heavyImpact() = perform(HapticFeedbackType.HeavyImpact)
    fun selection() = perform(HapticFeedbackType.Selection)
    fun success() = perform(HapticFeedbackType.Success)
    fun warning() = perform(HapticFeedbackType.Warning)
    fun error() = perform(HapticFeedbackType.Error)
}

/**
 * Получить провайдер тактильного отклика для текущей платформы.
 * - iOS: нативный Taptic Engine (UIImpactFeedbackGenerator, UISelectionFeedbackGenerator, UINotificationFeedbackGenerator).
 * - Android: системный Vibrator / VibrationEffect.
 */
expect fun getHapticFeedback(): HapticFeedbackProvider
