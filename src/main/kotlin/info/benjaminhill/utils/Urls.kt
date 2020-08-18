package info.benjaminhill.utils

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.zip.GZIPInputStream

private val gson: Gson by lazy {
    Gson()
}

/**
 * Encapsulate a URL download and convert to object using Gson
 * Supports nested Data Classes, and nullable lists.
 */
fun <T> URL.getToObject(classType: Class<T>): T {
    val con = this.openConnection()!!
    con.setRequestProperty("Accept-Encoding", "gzip")

    val contentJson = con.getInputStream().let {
        if ("gzip" == con.contentEncoding) {
            GZIPInputStream(it)
        } else {
            it
        }
    }.let {
        InputStreamReader(it).buffered()
    }.use(BufferedReader::readText)

    return gson.fromJson(contentJson, classType)!!
}