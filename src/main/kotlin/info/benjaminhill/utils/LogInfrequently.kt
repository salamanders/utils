package info.benjaminhill.utils

import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

val infrequentLogger = KotlinLogging.logger {}

/**
 * Spit out `private val logger = KotlinLogging.logger {}` `logger.info` log lines every few seconds
 */
@OptIn(ExperimentalTime::class)
class LogInfrequently constructor(
    private val delay: Duration = 10.seconds,
    private val logLine: (perSec: Double) -> String = { perSec: Double -> "Running at ${perSec.r}/sec" }
) {
    private var startTime = TimeSource.Monotonic.markNow()
    private var hitCount = AtomicLong()

    /**
     * If more than delay, logs line to logger.info
     */
    fun hit() {
        hitCount.incrementAndGet()
        if (startTime.elapsedNow() > delay) {
            try {
                infrequentLogger.info {
                    logLine(
                        hitCount.toDouble() / startTime.elapsedNow().toDouble(DurationUnit.SECONDS)
                    )
                }
            } catch (e: Error) {
                infrequentLogger.error { "Error when trying to log: $e" }
            }
            hitCount.set(0)
            startTime = TimeSource.Monotonic.markNow()
        }
    }
}


/** Print the line if the lineNum is a power of 2.  Good for series that might be big. */
inline fun logExp(lineNum: Int, crossinline log: () -> String) {
    if ((lineNum and (lineNum - 1)) == 0) {
        infrequentLogger.info { log() }
    }
}