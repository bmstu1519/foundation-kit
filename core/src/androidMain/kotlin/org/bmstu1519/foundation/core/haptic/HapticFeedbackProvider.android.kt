package org.bmstu1519.foundation.core.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AndroidHapticFeedbackProvider(private val context: Context) : HapticFeedbackProvider {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator ?: context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun perform(type: HapticFeedbackType) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = when (type) {
                    HapticFeedbackType.LightImpact -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    HapticFeedbackType.MediumImpact -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    HapticFeedbackType.HeavyImpact -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    HapticFeedbackType.Selection -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    HapticFeedbackType.Success -> VibrationEffect.createWaveform(longArrayOf(0, 20, 80, 30), intArrayOf(0, 140, 0, 220), -1)
                    HapticFeedbackType.Warning -> VibrationEffect.createWaveform(longArrayOf(0, 30, 80, 30), intArrayOf(0, 180, 0, 180), -1)
                    HapticFeedbackType.Error -> VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 40, 60, 40), intArrayOf(0, 220, 0, 220, 0, 220), -1)
                }
                vib.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticFeedbackType.LightImpact -> VibrationEffect.createOneShot(10, 80)
                    HapticFeedbackType.MediumImpact -> VibrationEffect.createOneShot(20, 160)
                    HapticFeedbackType.HeavyImpact -> VibrationEffect.createOneShot(35, 255)
                    HapticFeedbackType.Selection -> VibrationEffect.createOneShot(8, 60)
                    HapticFeedbackType.Success -> VibrationEffect.createWaveform(longArrayOf(0, 20, 80, 30), intArrayOf(0, 140, 0, 220), -1)
                    HapticFeedbackType.Warning -> VibrationEffect.createWaveform(longArrayOf(0, 30, 80, 30), intArrayOf(0, 180, 0, 180), -1)
                    HapticFeedbackType.Error -> VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 40, 60, 40), intArrayOf(0, 220, 0, 220, 0, 220), -1)
                }
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticFeedbackType.LightImpact, HapticFeedbackType.Selection -> vib.vibrate(10)
                    HapticFeedbackType.MediumImpact -> vib.vibrate(20)
                    HapticFeedbackType.HeavyImpact -> vib.vibrate(35)
                    HapticFeedbackType.Success, HapticFeedbackType.Warning -> vib.vibrate(longArrayOf(0, 25, 80, 25), -1)
                    HapticFeedbackType.Error -> vib.vibrate(longArrayOf(0, 35, 60, 35, 60, 35), -1)
                }
            }
        } catch (_: Throwable) {
            // Игнорируем возможные системные ошибки устройства
        }
    }
}

object NoOpHapticFeedbackProvider : HapticFeedbackProvider {
    override fun perform(type: HapticFeedbackType) = Unit
}

object HapticFeedbackProviderFactory {
    private var instance: HapticFeedbackProvider? = null

    fun initialize(context: Context): HapticFeedbackProvider {
        return instance ?: synchronized(this) {
            instance ?: AndroidHapticFeedbackProvider(context.applicationContext).also { instance = it }
        }
    }

    fun getInstance(): HapticFeedbackProvider = instance ?: NoOpHapticFeedbackProvider
}

fun initializeHaptics(context: Context): HapticFeedbackProvider =
    HapticFeedbackProviderFactory.initialize(context)

actual fun getHapticFeedback(): HapticFeedbackProvider =
    HapticFeedbackProviderFactory.getInstance()
