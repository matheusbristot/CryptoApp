package dev.bristot.cryptoapp.logger

internal class DefaultCryptoLogger(
    private val tag: String,
    private val logWriter: LogWriter,
) : CryptoLogger {
    override fun debug(message: String, throwable: Throwable?) {
        logWriter.debug(tag, message, throwable)
    }

    override fun warning(message: String, throwable: Throwable?) {
        logWriter.warning(tag, message, throwable)
    }

    override fun error(throwable: Throwable, message: String) {
        logWriter.error(tag, message, throwable)
    }
}
