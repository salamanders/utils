package info.benjaminhill.utils

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger

internal class SimpleCacheTest {


    @Test
    fun getSize() {
        val cacheFile = Files.createTempFile("t1", "gz").toFile().apply {
            deleteOnExit()
        }
        val sc = SimpleCache<String, Int>(cacheFile = cacheFile)
        sc["a"] = 1
        sc["b"] = 2
        assertEquals(2, sc.size)
    }

    @Test
    fun set() {
        val cacheFile = Files.createTempFile("t1", "gz").toFile().apply {
            deleteOnExit()
        }
        val sc = SimpleCache<String, Int>(cacheFile = cacheFile)
        sc["a"] = 1
        sc["b"] = 3
        assertEquals(1, sc["a"])
        assertEquals(3, sc["b"])
    }

    @Test
    fun testInvoke() {
        val cacheFile = Files.createTempFile("t1", "gz").toFile().apply {
            deleteOnExit()
        }
        val sc = SimpleCache<String, Int>(cacheFile = cacheFile)
        sc["a"] = 1
        sc["b"] = 3

        val counter = AtomicInteger()
        runBlocking {
            val c1 = sc("c") { counter.incrementAndGet() }
            assertEquals(1, c1)
            val c2 = sc("c") { counter.incrementAndGet() }
            assertEquals(1, c2)
            assertEquals(1, counter.get())
        }
    }
}