package org.bmstu1519.foundation.core.haptic

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

class IosHapticFeedbackProvider : HapticFeedbackProvider {
    private val lightImpactGen by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight) }
    private val mediumImpactGen by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium) }
    private val heavyImpactGen by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy) }
    private val selectionGen by lazy { UISelectionFeedbackGenerator() }
    private val notificationGen by lazy { UINotificationFeedbackGenerator() }

    override fun perform(type: HapticFeedbackType) {
        when (type) {
            HapticFeedbackType.LightImpact -> {
                lightImpactGen.prepare()
                lightImpactGen.impactOccurred()
            }
            HapticFeedbackType.MediumImpact -> {
                mediumImpactGen.prepare()
                mediumImpactGen.impactOccurred()
            }
            HapticFeedbackType.HeavyImpact -> {
                heavyImpactGen.prepare()
                heavyImpactGen.impactOccurred()
            }
            HapticFeedbackType.Selection -> {
                selectionGen.prepare()
                selectionGen.selectionChanged()
            }
            HapticFeedbackType.Success -> {
                notificationGen.prepare()
                notificationGen.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            }
            HapticFeedbackType.Warning -> {
                notificationGen.prepare()
                notificationGen.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
            }
            HapticFeedbackType.Error -> {
                notificationGen.prepare()
                notificationGen.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
            }
        }
    }
}

private val iosHapticInstance = IosHapticFeedbackProvider()

actual fun getHapticFeedback(): HapticFeedbackProvider = iosHapticInstance
