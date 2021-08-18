package info.benjaminhill.utils

import java.math.RoundingMode

fun Double.round(scale: Int = 1) = this.toBigDecimal().setScale(scale, RoundingMode.HALF_EVEN).toDouble()
fun Float.round(scale: Int = 1) = this.toBigDecimal().setScale(scale, RoundingMode.HALF_EVEN).toDouble()

/**
 * Fixed-width rounded Double
 */
val Double.r: String
    get() = this.round(3).toBigDecimal().toPlainString().let {
        if (it[0] == '-') {
            it
        } else {
            " $it"
        }
    }

val Float.r: String
    get() = this.round(3).toBigDecimal().toPlainString().let {
        if (it[0] == '-') {
            it
        } else {
            " $it"
        }
    }