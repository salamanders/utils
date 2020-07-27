package info.benjaminhill.utils

import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds
import kotlin.time.seconds

private val logger = KotlinLogging.logger {}

/**
 * Spit out `private val logger = KotlinLogging.logger {}` `logger.info` log lines every few seconds
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
                logger.info { logLine(hitPerSecond) }
                hitCount.set(0)
                startTimeNs = now
            }
        }
    }
}

/** Print the line if the lineNum is a power of 2.  Good for series that might be big. */
fun logexp(lineNum: Int, log: () -> String) {
    if ((lineNum and (lineNum - 1)) == 0) {
        logger.info { log() }
    }
}