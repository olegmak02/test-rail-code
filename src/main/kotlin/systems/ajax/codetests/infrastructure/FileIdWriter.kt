package systems.ajax.codetests.infrastructure

import org.slf4j.LoggerFactory
import systems.ajax.codetests.application.extensions.isValidFile
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils.ID
import java.io.File

object FileIdWriter {

    fun writeIdToCaseFile(id: Int, urlToFeature: String) {
        val featureFile = File(urlToFeature)
        if (featureFile.isValidFile()) {
            writeToTheFile(id, featureFile)
        } else {
            log.warn("Invalid .feature file path! {}", urlToFeature)
        }
    }

    private fun writeToTheFile(
        id: Int,
        metaSectionFile: File,
    ) {
        val lines = metaSectionFile.readLines().toMutableList()

        if (isLinesEmptyOrMissingID(lines)) {
            lines.addFirst("$ID${ExtractionUtils.DELIMITER}$id")
            metaSectionFile.writeText(lines.joinToString(System.lineSeparator()))
        }
    }

    private fun isLinesEmptyOrMissingID(lines: MutableList<String>) = lines.isEmpty() || !lines[0].contains(ID)

    private val log = LoggerFactory.getLogger(FileIdWriter::class.java)
}
