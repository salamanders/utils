package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimplePrefKtTest {

    private val mySetting1: Int by preference(
        enclosingClass = this::class.java,
        key = "MY_SETTING_1",
        defaultValue = -1
    )

    private var mySetting2: Int by preference(
        enclosingClass = this::class.java,
        key = "MY_SETTING_2",
        defaultValue = -1
    )

    @Test
    fun testPrefs() {
        mySetting2 = 5
        assertEquals(-1, mySetting1)
        assertEquals(5, mySetting2)
        mySetting2 = 7
        assertEquals(7, mySetting2)
    }
}