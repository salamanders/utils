package info.benjaminhill.utils

import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.seconds


/**
 * Spit out `private val logger = KotlinLogging.logger {}` `logger.info` log lines every few seconds
 */
class LogInfrequently @ExperimentalTime constructor(
    private val delay: Duration = 10.seconds
) {
    @ExperimentalTime
    private var startTime = TimeSource.Monotonic.markNow()
    private var hitCount = AtomicLong()

    /**
     * If more than delay, logs line to logger.info
     * Call with `linf.hit() { "Optional custom line to log" }`
     */
    @ExperimentalTime
    fun hit(
        logLine: () -> String = {
            "Running at ${(hitCount.toDouble() / startTime.elapsedNow().inSeconds).r}/sec"
        }
    ) {
        hitCount.incrementAndGet()

        if (startTime.elapsedNow() > delay) {
            logger.info { logLine() }
            hitCount.set(0)
            startTime = TimeSource.Monotonic.markNow()
        }
    }

    companion object : Logging
}

val logger by lazy { logger("logexp") }

/** Print the line if the lineNum is a power of 2.  Good for series that might be big. */
inline fun logexp(lineNum: Int, crossinline log: () -> String) {
    if ((lineNum and (lineNum - 1)) == 0) {
        logger.info { log() }
    }
}