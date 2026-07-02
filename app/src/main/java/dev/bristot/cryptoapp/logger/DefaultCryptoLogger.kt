package dev.bristot.cryptoapp.logger

import android.util.Log
import javax.inject.Inject

internal class DefaultCryptoLogger @Inject constructor(
    private val tag: String
) : CryptoLogger {
    override fun debug(message: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun warning(message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun error(throwable: Throwable, message: String) {
        Log.e(tag, message, throwable)
    }
}