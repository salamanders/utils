package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

internal class LocalFileKtTest {

    @Test
    fun getFileFail() {
        assertThrows(FileNotFoundException::class.java) { val f = getFile("fileDoesNotExist.txt") }
    }

    @Test
    fun getFile() {
        val f = getFile("pom.xml")
    }

    @Test
    fun getFileResources() {
        val f = getFile("src/test/resources/fileInResources.txt")
    }

    @Test
    fun getFileResourcesBad() {
        assertThrows(FileNotFoundException::class.java) {
            val f = getFile("resources/fileInResources.txt")
        }
    }
}