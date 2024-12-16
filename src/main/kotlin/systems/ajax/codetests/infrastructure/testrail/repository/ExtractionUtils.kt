package systems.ajax.codetests.infrastructure.testrail.repository

import kotlinx.serialization.json.Json
import systems.ajax.codetests.infrastructure.testrail.repository.entity.Meta
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.regex.Pattern

object ExtractionUtils {
    const val DELIMITER = ":"
    const val ID = "@id"

    fun extractIdFromDeletedFile(filePath: String): Int? {
        val process = ProcessBuilder("git", "show", "HEAD:$filePath")
            .redirectErrorStream(true)
            .start()

        val content = StringBuilder()
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.lines().forEach { line -> content.appendLine(line) }
        }

        val regex = Pattern.compile("\"id\"\\s*:\\s*(\\d+)|@id:(\\d+)")
        val matcher = regex.matcher(content.toString())
        return matcher.takeIf { matcher.find() }?.let {
            matcher.group(1)?.toIntOrNull() ?: matcher.group(2)?.toIntOrNull()
        }
    }

    fun extractSectionIdFromFile(url: String): Int? {
        return File(url).takeIf { it.exists() }?.let {
            Json.Default.decodeFromString<Meta>(it.readText()).id
        }
    }
}
