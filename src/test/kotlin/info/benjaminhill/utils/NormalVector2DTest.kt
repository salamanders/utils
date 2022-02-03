package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class NormalVector2DTest {
    private val nv0 = NormalVector2D(.3, .5)
    private val nv1 = NormalVector2D(.2, .2)


    @Test
    fun addN() {
        val output = nv0.addN(nv1)
        assertEquals(0.5, output.x, 0.001)
        assertEquals(.7, output.y, 0.001)
    }

    @Test
    fun subtractN() {
        assertThrows(IllegalArgumentException::class.java) {
            nv1.subtractN(nv0)
        }
    }

    @Test
    fun scalarMultiplyN() {
        val output = nv0.scalarMultiplyN(.3)
        assertEquals(0.09, output.x, 0.001)
        assertEquals(0.15, output.y, 0.001)
    }

    @Test
    fun normalizeN() {
        val output = nv0.normalizeN()
        assertEquals(0.5145, output.x, 0.001)
        assertEquals(0.8574, output.y, 0.001)
    }
}