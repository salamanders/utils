package info.benjaminhill.utils

import java.util.concurrent.ConcurrentSkipListSet

/** For pasting into spreadsheets */

/** Grows without bound */
private val alreadySeen: MutableSet<String> = ConcurrentSkipListSet()

/** Keys = only the strings, print each line at most once */
fun printlntOnce(vararg elements: Any?) {
    if (elements.size == 1 && elements[0] is Collection<Any?>) {
        return printlntOnce(*(elements[0] as Collection<Any?>).toTypedArray())
    }
    val key = elements.filterIsInstance<String>().joinToString(",")

    check(key.isNotBlank()) { "Bad key for printtsvOnce, need at least 1 String" }
    if (!alreadySeen.contains(key)) {
        alreadySeen.add(key)
        printlnt(*elements)
    }
}

fun printlnt(vararg elements: Any?) {
    if (elements.size == 1 && elements[0] is Collection<Any?>) {
        return printlnt(*(elements[0] as Collection<Any?>).toTypedArray())
    }
    return println(
        elements.joinToString("\t") {
            when (it) {
                is Double -> it.round(1)
                else -> it
            }.toString()
        }
    )
}
