package systems.ajax.codetests.infrastructure.testrail.mapper

import client.testrail.model.Section
import org.junit.jupiter.api.Test
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.infrastructure.testrail.mapper.SectionMapper.toAppView
import systems.ajax.codetests.infrastructure.testrail.mapper.SectionMapper.toTestrailView
import kotlin.test.assertEquals

class SectionMapperTest {

    @Test
    fun `should return app section`() {
        // GIVEN
        val appSection = AppSection(
            2,
            "testfolder",
            "Noting",
            1
        )
        val excepted = Section()
            .setId(2)
            .setName("testfolder")
            .setDescription("Noting")
            .setParentId(1)

        // WHEN
        val actual = appSection.toTestrailView()

        // THEN
        assertEquals(excepted, actual)
    }

    @Test
    fun `should return testrail section`() {
        // GIVEN
        val expected = AppSection(
            2,
            "testfolder",
            "Noting",
            1
        )
        val testrailSection = Section()
            .setId(2)
            .setName("testfolder")
            .setDescription("Noting")
            .setParentId(1)

        // WHEN
        val actual = testrailSection.toAppView()

        // THEN
        assertEquals(expected, actual)
    }
}
