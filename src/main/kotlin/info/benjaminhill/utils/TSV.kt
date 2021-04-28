package info.benjaminhill.utils

import java.util.concurrent.ConcurrentSkipListSet

/** For pasting into spreadsheets */

/** Grows without bound */
private val alreadySeen: MutableSet<String> = ConcurrentSkipListSet()

/** Keys = only the strings, print each line at most once */
fun printlntOnce(vararg elements: Any?) = printlntOnceFull(true, *elements)

fun printlntOnceSkip(vararg elements: Any?) = printlntOnceFull(false, *elements)

private fun printlntOnceFull(display: Boolean = true, vararg elements: Any?) {
    if (elements.isEmpty()) {
        return
    }
    val key = elements.filterIsInstance<String>().joinToString(",")
    require(key.isNotBlank()) { "Bad key for printtsvOnce, need at least 1 String: `${elements.joinToString { it?.javaClass?.kotlin?.toString() ?: "" }}`" }

    if (alreadySeen.add(key)) {
        if (display) {
            printlnt(*elements)
        }
    }
}

fun printlnt(vararg elements: Any?) {
    if (elements.isEmpty()) {
        return
    }
    require(elements.size > 1 || elements[0] !is Collection<*>) { "Do not pass a single collection: instead use the spread (*) operator." }
    return println(
        elements.joinToString("\t") {
            when (it) {
                is Double -> it.round(1)
                else -> it
            }.toString()
        }
    )
}
