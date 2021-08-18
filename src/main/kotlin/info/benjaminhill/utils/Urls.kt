package info.benjaminhill.utils

import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

private val gson: Gson by lazy {
    Gson()
}

/** Optimization: request (optional) gzipped content, and decompress it to Lines if available. */
fun URL.readLinesGZip(): List<String> = (openConnection() as HttpURLConnection).let { con ->
    con.setRequestProperty("Accept-Encoding", "gzip")
    if ("gzip" == con.contentEncoding) {
        LOG.debug { "Able to read GZIP content from '${this}'" }
        InputStreamReader(GZIPInputStream(con.inputStream))
    } else {
        LOG.debug { "No GZIP, fallback to plain content from '${this}'" }
        InputStreamReader(con.inputStream)
    }
}.readLines()

/**
 * Encapsulate a URL download and convert to object using Gson
 * Supports nested Data Classes, and nullable lists.
 */
fun <T> URL.getToObject(classType: Class<T>): T = gson.fromJson(this.readLinesGZip().joinToString("\n"), classType)!!

/** Danger: HTTP requestMethod=DELETE request can delete things! */
fun URL.delete(): Int = (openConnection() as HttpURLConnection).let { con ->
    con.requestMethod = "DELETE"
    return con.responseCode
}