package info.benjaminhill.utils

import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class TSVKtTest {

    @Test
    fun printlntOnceDirect() {
        printlntOnce(System.currentTimeMillis(), "phase", 123, Paths.get("."), 123.4)
    }

    @Test
    fun printlntOnce() {
        val now = System.currentTimeMillis()
        val logLine = arrayOf(now, "phase", 123, Paths.get("."), 123.4)
        repeat(10) { printlntOnce(*logLine) }
    }

    @Test
    fun printlntOnceSkipDirect() {
        printlntOnceSkip(System.currentTimeMillis(), "phase", 123, Paths.get("."), 123.4)
    }

    @Test
    fun printlntOnceSkip() {
        val logLine = arrayOf(System.currentTimeMillis(), "phase", 123, Paths.get("."), 123.4)
        printlntOnceSkip(*logLine)
    }

    @Test
    fun printlnt() {
        val logLine = listOf(System.currentTimeMillis(), "phase", 123, Paths.get("."), 123.4).toTypedArray()
        printlnt(*logLine)
    }
}