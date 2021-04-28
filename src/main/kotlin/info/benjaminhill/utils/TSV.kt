package info.benjaminhill.utils

import java.util.concurrent.ConcurrentSkipListSet

/** For pasting into spreadsheets */

/** Grows without bound */
private val alreadySeen: MutableSet<String> = ConcurrentSkipListSet()

/** avoid printing past garbage */
fun printlntOnceExclude(vararg elements: Any?) {
    alreadySeen.add(elements.filterIsInstance<String>().joinToString(","))
}

/** Keys = only the strings, print each line at most once */
fun printlntOnce(vararg elements: Any?) {
    if (elements.isEmpty()) {
        return
    }
    require(elements.size > 1 || elements[0] !is Collection<*>) { "Do not pass a collection, use the spread (*) operator." }

    val key = elements.filterIsInstance<String>().joinToString(",")
    check(key.isNotBlank()) { "Bad key for printtsvOnce, need at least 1 String" }
    if (!alreadySeen.contains(key)) {
        alreadySeen.add(key)
        printlnt(*elements)
    }
}

fun printlnt(vararg elements: Any?) {
    if (elements.isEmpty()) {
        return
    }
    require(elements.size > 1 || elements[0] !is Collection<*>) { "Do not pass a collection, use the spread (*) operator." }

    return println(
        elements.joinToString("\t") {
            when (it) {
                is Double -> it.round(1)
                else -> it
            }.toString()
        }
    )
}
