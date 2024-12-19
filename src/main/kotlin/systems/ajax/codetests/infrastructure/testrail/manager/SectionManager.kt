package systems.ajax.codetests.infrastructure.testrail.manager

import client.testrail.TestRail
import client.testrail.model.Section
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.port.output.SectionManagerOutPort
import systems.ajax.codetests.infrastructure.testrail.client.TestrailSectionWebClient
import systems.ajax.codetests.infrastructure.testrail.mapper.SectionMapper.toTestrailView
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils

@Component
internal class SectionManager(
    private val testrail: TestRail,
    private val testrailSectionWebClient: TestrailSectionWebClient,
    @Value("\${testrail.project.id}") private val projectId: Int,
    @Value("\${testrail.suite.id}") private val suiteId: Int
) : SectionManagerOutPort {

    override fun add(appSection: AppSection): Int {
        val newSection = appSection.toTestrailView()
        val response: Section = testrail.sections().add(projectId, newSection.setSuiteId(suiteId)).execute()
        return response.id
    }

    override fun update(appSection: AppSection) {
        testrail.sections().update(appSection.toTestrailView()).execute()
    }

    override fun delete(filePath: FilePath) {
        val extractIdFromDeletedFile = ExtractionUtils.extractIdFromDeletedFile(filePath)
        if (extractIdFromDeletedFile != null) {
            testrail.sections().delete(extractIdFromDeletedFile).execute()
        }
    }

    @Suppress("MapGetWithNotNullAssertionOperator", "UnsafeCallOnNullableType")
    override fun move(appSection: AppSection) {
        testrailSectionWebClient.move(appSection.id!!, appSection.parentId)
        update(appSection)
    }
}
