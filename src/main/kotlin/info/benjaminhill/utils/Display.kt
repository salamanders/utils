package info.benjaminhill.utils

import java.io.IOException
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

/** Dumps a throwable to the output */
fun Throwable.printDeepStackTrace() {
    println(this)
    try {
        this.stackTrace?.also { it: Array<StackTraceElement> ->
            println("${this.javaClass}: ${this.message}")
        }?.forEach { trace ->
            println("  at ${trace.className}.${trace.methodName} (${trace.fileName}:${trace.lineNumber})")
        }
        var cause: Throwable? = this.cause
        while (null != cause) {
            cause.stackTrace?.also {
                println("Caused By: ${cause?.javaClass}: ${cause?.message}")
            }?.forEach { stackTraceElement ->
                println("  at ${stackTraceElement.className}.${stackTraceElement.methodName} (${stackTraceElement.fileName}:${stackTraceElement.lineNumber}")
            }
            cause = cause.cause
        }
    } catch (ex: IOException) {
        this.printStackTrace()
    }
}