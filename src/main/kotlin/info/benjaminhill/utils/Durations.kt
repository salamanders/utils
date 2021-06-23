package info.benjaminhill.utils

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * "[[[hh:]mm:]ss]" to Duration
 */
@ExperimentalTime
fun Duration.Companion.hms(value: String): Duration {
    val parts = value.split(":").reversed()
    var result = seconds(0)
    if (parts.isNotEmpty()) {
        result += seconds(parts[0].toLong())
    }
    if (parts.size > 1) {
        result += minutes(parts[1].toLong())
    }
    if (parts.size > 2) {
        result += hours(parts[2].toLong())
    }
    return result
}