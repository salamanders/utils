package info.benjaminhill.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(scale: Int = 1) = BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()

/**
 * Fixed-width rounded Double
 */
val Double.r: String
    get() = this.round(4).toBigDecimal().toPlainString().let {
        if (it[0] == '-') {
            it
        } else {
            " $it"
        }
    }

