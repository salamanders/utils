package info.benjaminhill.utils

import kotlin.time.*

/**
 * "[[hh:]mm:]ss]" to Duration
 */
@ExperimentalTime
val String.hms: Duration
    get() {
        val parts = split(":").reversed()
        var result = 0.seconds
        if (parts.isNotEmpty()) {
            result += parts[0].toLong().seconds
        }
        if (parts.size > 1) {
            result += parts[1].toLong().minutes
        }
        if (parts.size > 2) {
            result += parts[2].toLong().hours
        }
        return result
    }


