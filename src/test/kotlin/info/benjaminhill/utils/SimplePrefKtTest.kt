package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.prefs.Preferences

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

    @BeforeEach
    internal fun setUp() {
        val prefs = Preferences.userNodeForPackage(this::class.java)
        prefs.clear()
        mySetting2 = 5
    }

    @Test
    fun testPrefs() {
        assertEquals(-1, mySetting1)
        assertEquals(5, mySetting2)
        mySetting2 = 7
        assertEquals(7, mySetting2)
    }
}