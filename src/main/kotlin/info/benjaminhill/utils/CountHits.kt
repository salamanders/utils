package info.benjaminhill.utils

import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

/**
 * Spit out println log lines every hitFrequency calls to hit()
 */
class CountHits(
    private val hitFrequency: Int = 5_000,
    private val logLine: (perSec: Int) -> String = { perSec: Int -> "Running at $perSec/sec" }
) {

    @ExperimentalTime
    private var startTimeNs = System.nanoTime().nanoseconds
    private var hitCount = 0

    @ExperimentalTime
    fun hit() {
        hitCount++
        if (hitCount >= hitFrequency) {
            val nowNs = System.nanoTime().nanoseconds
            val elapsedTime = nowNs - startTimeNs
            val hitPerSecond = hitCount.toDouble() / elapsedTime.inSeconds
            println(logLine(hitPerSecond.toInt()))
            hitCount = 0
            startTimeNs = nowNs
        }
    }
}