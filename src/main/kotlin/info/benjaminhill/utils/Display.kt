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

