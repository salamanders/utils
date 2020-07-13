package info.benjaminhill.utils

import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds
import kotlin.time.seconds

/**
 * Spit out println log lines every few seconds
 */
class LogInfrequently @ExperimentalTime constructor(
    private val delay: Duration = 10.seconds,
    private val logLine: (perSec: Double) -> String = { perSec: Double -> "Running at ${perSec.r}/sec" }
) {
    @ExperimentalTime
    private var startTimeNs = System.nanoTime().nanoseconds
    private var hitCount = AtomicLong()


    @ExperimentalTime
    fun hit() {
        hitCount.incrementAndGet()
        System.nanoTime().nanoseconds.let { now ->
            if (now - startTimeNs > delay) {
                val elapsedTime = now - startTimeNs
                val hitPerSecond = hitCount.toDouble() / elapsedTime.inSeconds
                println(logLine(hitPerSecond))
                hitCount.set(0)
                startTimeNs = now
            }
        }
    }
}