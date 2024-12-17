package systems.ajax.codetests.infrastructure.testrail.manager

import client.testrail.TestRail
import client.testrail.model.Section
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.infrastructure.testrail.client.TestrailSectionWebClient
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class SectionManagerTest {
    @MockK
    private lateinit var sections: TestRail.Sections

    @MockK
    private lateinit var testRail: TestRail

    @MockK
    private lateinit var update: TestRail.Sections.Update

    @MockK
    private lateinit var delete: TestRail.Sections.Delete

    @MockK
    private lateinit var add: TestRail.Sections.Add

    @MockK
    private lateinit var testrailSectionWebClient: TestrailSectionWebClient

    private lateinit var sectionManager: SectionManager

    @BeforeEach
    fun setUp() {
        sectionManager = SectionManager(testRail, testrailSectionWebClient, PROJECT_ID)
    }

    @Test
    fun `should add section`() {
        // GIVEN
        every {
            testRail.sections()
        } returns sections
        every {
            sections.add(PROJECT_ID, testrailSection)
        } returns add
        every {
            add.execute()
        } returns newSection

        // WHEN
        val actual = sectionManager.add(appSection)

        // THEN
        verify(exactly = 1) { testRail.sections() }
        verify(exactly = 1) { sections.add(PROJECT_ID, testrailSection) }
        verify(exactly = 1) { add.execute() }
        assertEquals(newSection.id, actual)
    }

    @Test
    fun `should update section`() {
        // GIVEN
        every {
            testRail.sections()
        } returns sections
        every {
            sections.update(testrailSection)
        } returns update
        justRun {
            update.execute()
        }

        // WHEN
        sectionManager.update(appSection)

        // THEN
        verify(exactly = 1) { testRail.sections() }
        verify(exactly = 1) { sections.update(testrailSection) }
        verify(exactly = 1) { update.execute() }
    }

    @Test
    fun `should remove section`() {
        // GIVEN
        val returnedId = 1
        val filePath = FilePath("path")
        mockkObject(ExtractionUtils)
        every {
            testRail.sections()
        } returns sections
        every {
            sections.delete(returnedId)
        } returns delete
        justRun {
            delete.execute()
        }
        every {
            ExtractionUtils.extractIdFromDeletedFile(filePath)
        } returns returnedId

        // WHEN
        sectionManager.delete(filePath)

        // THEN
        verify(exactly = 1) { testRail.sections() }
        verify(exactly = 1) { sections.delete(returnedId) }
        verify(exactly = 1) { delete.execute() }
        verify(exactly = 1) { ExtractionUtils.extractIdFromDeletedFile(filePath) }
    }

    @Test
    fun `should move and rename section`() {
        // GIVEN
        every {
            testRail.sections()
        } returns sections
        every {
            testrailSectionWebClient.move(appSection.id!!, appSection.parentId!!)
        } returns newSection
        every {
            sections.update(testrailSection)
        } returns update
        justRun {
            update.execute()
        }

        // WHEN
        sectionManager.move(appSection)

        // THEN
        verify(exactly = 1) { testRail.sections() }
        verify(exactly = 1) {
            testrailSectionWebClient.move(
                appSection.id!!,
                appSection.parentId!!,
            )
        }
    }

    private companion object {
        private const val PROJECT_ID: Int = 1

        private val appSection = AppSection(
            2,
            "testfolder",
            "Noting",
            1
        )
        private val testrailSection = Section()
            .setId(2)
            .setName("testfolder")
            .setDescription("Noting")
            .setParentId(1)
        private val newSection = Section()
            .setId(3)
    }
}
