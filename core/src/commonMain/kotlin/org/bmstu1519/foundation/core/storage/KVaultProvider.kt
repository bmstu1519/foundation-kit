package org.bmstu1519.foundation.core.storage

import com.liftric.kvault.KVault

interface KVaultProvider {
    val kVault: KVault
}

expect fun getKVaultInstance(): KVaultProvider
