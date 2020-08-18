package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.net.URL

internal class UrlsKtTest {

    @Test
    fun getToObject() {
        val result = URL("http://echo.jsontest.com/key/2/one/two")
            .getToObject(TestResult::class.java)
        assertEquals("two", result.one)
        assertEquals(2, result.key)
        assertNull(result.notHere)
    }

    data class MissingStuff(
        val name: String
    )

    data class TestResult(
        val one: String,
        val key: Int,
        val notHere: List<MissingStuff>?
    )
}