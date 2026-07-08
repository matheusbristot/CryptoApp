package dev.bristot.cryptoapp.logger

interface CryptoLogger {
    fun debug(message: String, throwable: Throwable? = null)
    fun warning(message: String, throwable: Throwable? = null)
    fun error(throwable: Throwable, message: String)
}
