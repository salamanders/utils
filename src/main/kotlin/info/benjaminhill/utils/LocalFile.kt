package info.benjaminhill.utils

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

/**
 * Tries a variety of methods to locate a local file
 * Looks in resources, local working directory, user dir, getResources
 * @param path Relative path to a file
 */
fun getFile(path: String): File {
    try {
        return listOfNotNull(
            File(path),
            Paths.get(path).toFile(),
            Paths.get("resources", path).toFile(),
            File(File(".").canonicalPath, path),
            File(System.getProperty("user.dir"), path),
            (object {}.javaClass.getResource(path))?.let { File(it.toURI()) }
        ).first { it.exists() }
    } catch (e:NoSuchElementException) {
        throw FileNotFoundException("Unable to find file: '$path'")
    }
}