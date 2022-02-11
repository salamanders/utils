package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

internal class LocalFileKtTest {

    @Test
    fun getFileFail() {
        assertThrows(FileNotFoundException::class.java) { getFile("fileDoesNotExist.txt") }
    }

    @Test
    fun getFile() {
        getFile("README.md")
    }

    @Test
    fun getFileResources() {
        getFile("src/test/resources/fileInResources.txt")
    }

    @Test
    fun getFileResourcesBad() {
        assertThrows(FileNotFoundException::class.java) {
            getFile("resources/fileInResources.txt")
        }
    }
}