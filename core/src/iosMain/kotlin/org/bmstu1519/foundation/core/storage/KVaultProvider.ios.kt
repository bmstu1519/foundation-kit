package org.bmstu1519.foundation.core.storage

import com.liftric.kvault.KVault

class KVaultInstanceIOS(name: String = DEFAULT_STORE_NAME) : KVaultProvider {
    override val kVault: KVault = KVault(name)
}

actual fun getKVaultInstance(): KVaultProvider = KVaultInstanceIOS()

private const val DEFAULT_STORE_NAME = "session-settings"
