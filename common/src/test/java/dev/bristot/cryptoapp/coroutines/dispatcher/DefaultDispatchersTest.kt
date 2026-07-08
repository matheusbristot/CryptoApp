package dev.bristot.cryptoapp.coroutines.dispatcher

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DefaultDispatchersTest {

    @Test
    fun dispatcherProvider_returnsDefaultCoroutineDispatchers() {
        val provider: DispatcherProvider = DefaultDispatchers()

        assertSame(Dispatchers.Main, provider.main)
        assertSame(Dispatchers.IO, provider.io)
        assertSame(Dispatchers.Default, provider.default)
    }

    @Test
    fun dispatcherModule_providesDispatcherProviderContract() {
        val provider: DispatcherProvider = DispatcherModule.provideDispatcherCoroutines()

        assertSame(Dispatchers.Default, provider.default)
    }
}
