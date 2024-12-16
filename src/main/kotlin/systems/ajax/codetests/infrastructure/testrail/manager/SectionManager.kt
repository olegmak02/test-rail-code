package systems.ajax.codetests.infrastructure.testrail.manager

import client.testrail.TestRail
import client.testrail.model.Section
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.port.output.SectionManagerOutPort
import systems.ajax.codetests.infrastructure.testrail.client.TestrailSectionWebClient
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils

@Component
internal class SectionManager(
    private val testrail: TestRail,
    private val testrailSectionWebClient: TestrailSectionWebClient,
    @Value("\${project.id}") private val projectId: Int,
    @Value("\${suite.id}") private val suiteId: Int,
) : SectionManagerOutPort {

    override fun add(appSection: AppSection): Int {
        val newSection = appSection.toTestrailView()
        val response: Section = testrail.sections().add(projectId, newSection).execute()
        return response.id
    }

    override fun update(appSection: AppSection) {
        testrail.sections().update(appSection.toTestrailView()).execute()
    }

    override fun delete(urlToTheFile: FilePath) {
        val extractIdFromDeletedFile = ExtractionUtils.extractIdFromDeletedFile(urlToTheFile.path)
        if (extractIdFromDeletedFile != null) {
            testrail.sections().delete(extractIdFromDeletedFile).execute()
        }
    }

    @Suppress("MapGetWithNotNullAssertionOperator", "UnsafeCallOnNullableType")
    override fun move(appSection: AppSection) {
        sectionsMap.ifEmpty {
            val sections = testrail.sections().list(projectId, suiteId).execute()
                .associateBy { section -> section.id }
            sectionsMap.putAll(sections)
        }

        val localSection = appSection.toTestrailView()
        val remoteSection: Section = sectionsMap[localSection.id]!!

        if (remoteSection.parentId != localSection.parentId) {
            testrailSectionWebClient.move(localSection.id, localSection.parentId)
        }

        if (remoteSection.name != localSection.name) {
            testrail.sections().update(localSection)
        }
    }

    companion object {
        val sectionsMap: MutableMap<Int, Section> = mutableMapOf()

        private fun AppSection.toTestrailView(): Section {
            val section = Section()
            id?.let { section.id = it }
            section
                .setName(name)
                .setParentId(parentId)
                .setDescription(description)
            return section
        }
    }
}
