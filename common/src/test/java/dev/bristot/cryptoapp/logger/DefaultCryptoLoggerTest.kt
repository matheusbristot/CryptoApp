package dev.bristot.cryptoapp.logger

import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultCryptoLoggerTest {

    @Test
    fun debug_delegatesMessageThrowableAndTagToLogWriter() {
        val writer = RecordingLogWriter()
        val throwable = Throwable("debug")
        val logger: CryptoLogger = DefaultCryptoLogger(tag = "TestTag", logWriter = writer)

        logger.debug(message = "debug message", throwable = throwable)

        assertEquals(
            RecordingLogWriter.LogCall.Debug(
                tag = "TestTag",
                message = "debug message",
                throwable = throwable,
            ),
            writer.calls.single(),
        )
    }

    @Test
    fun debug_usesNullThrowableByDefault() {
        val writer = RecordingLogWriter()
        val logger: CryptoLogger = DefaultCryptoLogger(tag = "TestTag", logWriter = writer)

        logger.debug(message = "debug message")

        val call = writer.calls.single() as RecordingLogWriter.LogCall.Debug
        assertEquals("debug message", call.message)
        assertEquals(null, call.throwable)
    }

    @Test
    fun warning_delegatesMessageThrowableAndTagToLogWriter() {
        val writer = RecordingLogWriter()
        val throwable = Throwable("warning")
        val logger: CryptoLogger = DefaultCryptoLogger(tag = "TestTag", logWriter = writer)

        logger.warning(message = "warning message", throwable = throwable)

        assertEquals(
            RecordingLogWriter.LogCall.Warning(
                tag = "TestTag",
                message = "warning message",
                throwable = throwable,
            ),
            writer.calls.single(),
        )
    }

    @Test
    fun error_delegatesMessageThrowableAndTagToLogWriter() {
        val writer = RecordingLogWriter()
        val throwable = Throwable("error")
        val logger: CryptoLogger = DefaultCryptoLogger(tag = "TestTag", logWriter = writer)

        logger.error(throwable = throwable, message = "error message")

        assertEquals(
            RecordingLogWriter.LogCall.Error(
                tag = "TestTag",
                message = "error message",
                throwable = throwable,
            ),
            writer.calls.single(),
        )
    }

    @Test
    fun loggerModule_providesCryptoLoggerContract() {
        val logger: CryptoLogger = LoggerModule.providesLogger()

        assertEquals(DefaultCryptoLogger::class, logger::class)
    }

    private class RecordingLogWriter : LogWriter {
        val calls = mutableListOf<LogCall>()

        override fun debug(tag: String, message: String, throwable: Throwable?) {
            calls += LogCall.Debug(tag = tag, message = message, throwable = throwable)
        }

        override fun warning(tag: String, message: String, throwable: Throwable?) {
            calls += LogCall.Warning(tag = tag, message = message, throwable = throwable)
        }

        override fun error(tag: String, message: String, throwable: Throwable) {
            calls += LogCall.Error(tag = tag, message = message, throwable = throwable)
        }

        sealed interface LogCall {
            val tag: String
            val message: String

            data class Debug(
                override val tag: String,
                override val message: String,
                val throwable: Throwable?,
            ) : LogCall

            data class Warning(
                override val tag: String,
                override val message: String,
                val throwable: Throwable?,
            ) : LogCall

            data class Error(
                override val tag: String,
                override val message: String,
                val throwable: Throwable,
            ) : LogCall
        }
    }
}
