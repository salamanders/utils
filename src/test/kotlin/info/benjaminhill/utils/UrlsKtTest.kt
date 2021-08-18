package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.net.URL

internal class UrlsKtTest {

    @Test
    fun getToObject() {
        val result = URL("https://filesamples.com/samples/code/json/sample1.json")
            .getToObject(TestResult::class.java)
        assertEquals("Apple", result.fruit)
        assertEquals("Large", result.size)
        assertNull(result.notHere)
    }

    @Test
    fun delete() {
        // Hope they don't let me delete this!
        val result = URL("https://filesamples.com/samples/code/json/sample1.json").delete()
        assertEquals(405, result)
    }

    data class MissingStuff(
        val name: String
    )

    data class TestResult(
        val fruit: String,
        val size: String,
        val notHere: List<MissingStuff>?
    )
}