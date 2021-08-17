package info.benjaminhill.utils

/**
 * Because things can fail.
 * TODO: Random falloff
 */
suspend fun <T> retryOrNull(maxRetries: Int = 1, f: suspend () -> T?): T? {
    var attemptNum = 0
    while (attemptNum <= maxRetries) {
        f()?.let { return it }
        attemptNum++
    }
    return null
}
