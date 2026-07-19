package dev.bristot.cryptoapp.time

fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
