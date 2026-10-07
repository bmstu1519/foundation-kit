package org.bmstu1519.foundation.core.storage

import android.content.Context
import com.liftric.kvault.KVault

class KVaultInstanceAndroid(context: Context, name: String = DEFAULT_STORE_NAME) : KVaultProvider {
    override val kVault: KVault = KVault(context, name)
}

object KVaultProviderFactory {
    private var instance: KVaultProvider? = null

    fun initialize(context: Context, name: String = DEFAULT_STORE_NAME): KVaultProvider {
        return instance ?: synchronized(this) {
            instance ?: KVaultInstanceAndroid(context.applicationContext, name).also { instance = it }
        }
    }

    fun getKVaultInstance(): KVaultProvider = instance
        ?: throw IllegalStateException("KVaultProvider is not initialized. Call KVaultProviderFactory.initialize(context) before using it.")
}

fun initializeKVault(context: Context, name: String = DEFAULT_STORE_NAME): KVaultProvider =
    KVaultProviderFactory.initialize(context, name)

actual fun getKVaultInstance(): KVaultProvider = KVaultProviderFactory.getKVaultInstance()

private const val DEFAULT_STORE_NAME = "session-settings"
