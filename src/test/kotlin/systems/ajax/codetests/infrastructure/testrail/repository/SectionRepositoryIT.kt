package systems.ajax.codetests.infrastructure.testrail.repository

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.infrastructure.testrail.repository.entity.Meta
import java.io.File
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SectionRepositoryIT {
    private val sectionFileRepository: SectionFileRepository = SectionFileRepository()

    @ParameterizedTest
    @MethodSource("arguments for should return valid section")
    fun `should return valid section`(url: String, expected: AppSection) {
        // GIVEN WHEN
        val actual: AppSection = sectionFileRepository.get(FilePath(url))

        // THEN
        assertEquals(expected, actual)
    }

    @Test
    fun `should write id to the meta section file`() {
        // GIVEN
        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure" +
            "/testrail/repository/testfilesforidwriter"
        val featureFileName = "features/_Meta.json"
        val idToWrite = 1
        val file = File(parentPath, featureFileName)
        val fileId = FileId(
            idToWrite,
            FilePath("$parentPath/$featureFileName")
        )
        file.createNewFile()

        // WHEN
        sectionFileRepository.writeIdToSectionFile(fileId)

        // THEN
        assertEquals(
            Json.decodeFromString<Meta>(file.readText()).id,
            idToWrite
        )
        file.delete()
    }

    @Test
    fun `should write ids to the meta section files only`() {
        // GIVEN
        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/" +
            "testrail/repository/testfilesforidwriter"
        val wrongFile = "wrongFile.feature"
        val idToWrite = 1
        val fileId = FileId(
            idToWrite,
            FilePath("$parentPath/$wrongFile")
        )

        // WHEN THEN
        sectionFileRepository.writeIdToSectionFile(fileId)
    }

    @Test
    fun `should do nothing when write to the non-existing meta section file`() {
        // GIVEN
        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/" +
            "testrail/repository/testfilesforidwriter"
        val idToWrite = 1
        val fileId = FileId(
            idToWrite,
            FilePath(parentPath)
        )

        // WHEN THEN
        sectionFileRepository.writeIdToSectionFile(fileId)
    }

    @Test
    fun `should do nothing when id exists in the meta section file`() {
        // GIVEN
        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/" +
            "testrail/repository/testfilesforidwriter"
        val metaFileName = "_MetaWithId.json"
        val idToWrite = 1
        val file = File(parentPath, metaFileName)
        val fileId = FileId(
            idToWrite,
            FilePath("$parentPath/$metaFileName")
        )

        // WHEN
        sectionFileRepository.writeIdToSectionFile(fileId)

        // THEN
        assertNotNull(
            Json.decodeFromString<Meta>(file.readText()).id,
            "Feature file should contain id"
        )
    }

    private companion object {
        private val childSection = AppSection(
            2,
            "testfolder",
            "Test",
            1,
        )
        private val parentSection = AppSection(
            1,
            "testdata",
            "Test",
            null
        )

        @JvmStatic
        fun `arguments for should return valid section`(): Stream<Arguments> = Stream.of<Arguments>(
            Arguments.of(
                "src/test/kotlin/systems/ajax/codetests/" +
                    "infrastructure/testrail/repository/testdata/_Meta.json",
                parentSection,
            ),
            Arguments.of(
                "src/test/kotlin/systems/ajax/codetests/infrastructure/testrail/" +
                    "repository/testdata/testfolder/_Meta.json",
                childSection,
            )
        )
    }
}
