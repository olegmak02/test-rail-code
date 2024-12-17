package systems.ajax.codetests.infrastructure.testrail.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.extensions.isValidFile
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.port.output.SectionRepositoryOutPort
import systems.ajax.codetests.infrastructure.testrail.repository.entity.Meta
import java.io.File

@Component
class SectionFileRepository : SectionRepositoryOutPort {

    override fun writeIdToSectionFile(fileId: FileId) {
        val metaSectionFile = File(fileId.filePath.path)

        if (metaSectionFile.isValidFile()) {
            val parsedData = readJson(metaSectionFile)
            val withUpdatedId = parsedData.copy(id = fileId.id)
            metaSectionFile.writeText(Json.encodeToString<Meta>(withUpdatedId))
        } else {
            log.warn("Invalid .section file path! {}", fileId.filePath)
        }
    }

    override fun get(filePath: FilePath): AppSection {
        val meta: Meta = readJson(File(filePath.path))
        return AppSection(
            meta.id,
            extractSectionName(filePath.path),
            meta.description,
            ExtractionUtils.extractSectionIdFromFile(extractParentFolder(filePath.path))
        )
    }

    private fun readJson(metaSectionFile: File): Meta {
        if (metaSectionFile.readText().isNotEmpty()) {
            return Json.decodeFromString<Meta>(metaSectionFile.readText())
        }
        return Meta()
    }

    private fun extractParentFolder(url: String): String = url.substringBeforeLast(File.separator)
        .substringBeforeLast(File.separator)
        .plus(File.separator)
        .plus(url.substringAfterLast(File.separator))

    private fun extractSectionName(url: String): String = url
        .substringBeforeLast(File.separator)
        .substringAfterLast(File.separator)

    companion object {
        private val log = LoggerFactory.getLogger(SectionFileRepository::class.java)
    }
}
