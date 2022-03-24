package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

private class PrefTest {
    val mySetting1: Int by preference(
        enclosingClass = this::class.java,
        key = "MY_SETTING_1",
        defaultValue = -1
    )

    var mySetting2: Int by preference(
        enclosingClass = this::class.java,
        key = "MY_SETTING_2",
        defaultValue = -1
    )

    var mySpecialSetting: Instant by preference(
        enclosingClass = this::class.java,
        key = "MY_SETTING_3",
        defaultValue = Instant.now()
    )
}

internal class SimplePrefKtTest {
    @Test
    fun testPrefs() {
        val prefObj = PrefTest()
        prefObj.mySetting2 = 5
        assertEquals(-1, prefObj.mySetting1)
        assertEquals(5, prefObj.mySetting2)
        prefObj.mySetting2 = 7
        assertEquals(7, prefObj.mySetting2)

        prefObj.mySpecialSetting = Instant.EPOCH
        println(prefObj.mySpecialSetting)
    }
}

