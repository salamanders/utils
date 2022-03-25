package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertContains

internal class UrlsKtTest {

    @Test
    fun getToObject() {
        val result = URL("https://raw.githubusercontent.com/LearnWebCode/json-example/master/animals-1.json")
            .getToObject(Array<Pet>::class.java)

        assertEquals(3, result.size)
        assertContains(arrayOf("cat", "dog"), result.first().species)
    }

    @Test
    fun delete() {
        // Hope they don't let me delete this!
        val result = URL("https://raw.githubusercontent.com/LearnWebCode/json-example/master/animals-1.json").delete()
        assertEquals(403, result)
    }


    data class Pet(
        val name: String,
        val species: String,
    )
}