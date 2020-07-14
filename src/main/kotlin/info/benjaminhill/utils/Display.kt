package info.benjaminhill.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(decimals: Int = 4): Double = "%.${decimals}f".format(this).toDouble()

/**
 * Fixed-width rounded Double
 */
val Double.r: String
    get() = BigDecimal(this).setScale(4, RoundingMode.HALF_EVEN).toPlainString().let {
        if (it[0] == '-') {
            it
        } else {
            " $it"
        }
    }

/** Print the line if the lineNum is a power of 2.  Good for series that might be big. */
fun println2(lineNum: Int, log: () -> String) {
    if ((lineNum and (lineNum - 1)) == 0) {
        println(log())
    }
}