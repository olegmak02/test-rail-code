package systems.ajax.codetests.application.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.port.output.SectionManagerOutPort
import systems.ajax.codetests.application.port.output.SectionRepositoryOutPort

@ExtendWith(MockKExtension::class)
class ApplyDiffSectionUseCaseTest {
    @MockK
    private lateinit var sectionRepositoryOutPort: SectionRepositoryOutPort

    @MockK
    private lateinit var sectionManagerOutPort: SectionManagerOutPort

    @InjectMockKs
    private lateinit var useCase: ApplyDiffSectionUseCase

    @Test
    fun `should execute add flow`() {
        // GIVEN
        var action = Action.ADDED
        val newAppSection = appSection.copy(id = null)
        justRun {
            sectionRepositoryOutPort.writeIdToSectionFile(
                match { it.id == ID && it.filePath == filePath }
            )
        }
        every {
            sectionRepositoryOutPort.get(filePath)
        } returns newAppSection
        every {
            sectionManagerOutPort.add(newAppSection)
        } returns ID

        // WHEN
        useCase.execute(filePath, action)

        // THEN
        verify(exactly = 1) { sectionManagerOutPort.add(newAppSection) }
        verify(exactly = 1) {
            sectionRepositoryOutPort
                .writeIdToSectionFile(match { it.id == ID && it.filePath == filePath })
        }
        verify(exactly = 1) { sectionRepositoryOutPort.get(filePath) }
    }

    @Test
    fun `should execute delete flow`() {
        // GIVEN
        var action = Action.REMOVED
        justRun {
            sectionManagerOutPort.delete(filePath)
        }

        // WHEN
        useCase.execute(filePath, action)

        // THEN
        verify(exactly = 1) { sectionManagerOutPort.delete(filePath) }
    }

    @Test
    fun `should execute update flow`() {
        // GIVEN
        var action = Action.MODIFIED
        every {
            sectionRepositoryOutPort.get(filePath)
        } returns appSection
        justRun {
            sectionManagerOutPort.update(appSection)
        }

        // WHEN
        useCase.execute(filePath, action)

        // THEN
        verify(exactly = 1) { sectionManagerOutPort.update(appSection) }
        verify(exactly = 1) { sectionRepositoryOutPort.get(filePath) }
    }

    @Test
    fun `should execute move flow`() {
        // GIVEN
        var action = Action.MOVED_OR_RENAMED
        every {
            sectionRepositoryOutPort.get(filePath)
        } returns appSection
        justRun {
            sectionManagerOutPort.move(appSection)
        }

        // WHEN
        useCase.execute(filePath, action)

        // THEN
        verify(exactly = 1) { sectionManagerOutPort.move(appSection) }
        verify(exactly = 1) { sectionRepositoryOutPort.get(filePath) }
    }

    @Test
    fun `should throw exception`() {
        // GIVEN
        var action = Action.UNKNOWN

        // WHEN THEN
        assertThrows<IllegalStateException> { useCase.execute(filePath, action) }
    }

    companion object {
        private const val ID: Int = 1
        private val appSection = AppSection(
            2,
            "testfolder",
            "Test",
            1,
        )
        private val filePath: FilePath = FilePath("path")
    }
}
