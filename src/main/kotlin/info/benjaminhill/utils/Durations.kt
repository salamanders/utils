package info.benjaminhill.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * "[[[hh:]mm:]ss]" to Duration
 */
inline val String.hms: Duration
    get(): Duration {
        val parts: List<String> = this.split(":").reversed()
        var totalDuration = 0.seconds
        if (parts.isNotEmpty() && parts[0].isNotBlank()) {
            totalDuration += parts[0].toLong().seconds
        }
        if (parts.size > 1 && parts[1].isNotBlank()) {
            totalDuration += parts[1].toLong().minutes
        }
        if (parts.size > 2 && parts[2].isNotBlank()) {
            totalDuration += parts[2].toLong().hours
        }
        return totalDuration
    }

