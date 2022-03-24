package info.benjaminhill.utils

/** Most frequent N strings in the last X samples */
class CounterFIFO(
    private val maxSize: Int = 1_000
) {
    private val counts = ArrayDeque<String>(maxSize + 1)
    fun inc(key: String) {
        counts.add(key)
        while (counts.size > maxSize) {
            counts.removeFirst()
        }
    }

    fun topN(n: Int = 3): Set<String> =
        counts.groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.take(n).map { it.first }.toSet()

}