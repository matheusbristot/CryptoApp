package dev.bristot.cryptoapp.logger

import android.util.Log

internal interface LogWriter {
    fun debug(tag: String, message: String, throwable: Throwable?)
    fun warning(tag: String, message: String, throwable: Throwable?)
    fun error(tag: String, message: String, throwable: Throwable)
}

internal object AndroidLogWriter : LogWriter {
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
}
