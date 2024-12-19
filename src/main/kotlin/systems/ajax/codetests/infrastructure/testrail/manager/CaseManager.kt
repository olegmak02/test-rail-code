package systems.ajax.codetests.infrastructure.testrail.manager

import client.testrail.TestRail
import client.testrail.model.Case
import client.testrail.model.CaseField
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.appcase.AppCase
import systems.ajax.codetests.application.port.output.CaseManagerOutPort
import systems.ajax.codetests.infrastructure.testrail.client.TestrailCaseWebClient
import systems.ajax.codetests.infrastructure.testrail.mapper.CaseMapper.toTestrailView
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils

@Component
internal class CaseManager(
    private val testrail: TestRail,
    private val testrailCaseWebClient: TestrailCaseWebClient
) : CaseManagerOutPort {

    override fun add(appCase: AppCase, customCaseFields: List<CaseField>): Int {
        val newCase = appCase.toTestrailView()
        val response: Case = testrail.cases()
            .add(newCase.sectionId, newCase, customCaseFields).execute()
        return response.id
    }

    override fun update(appCase: AppCase, customCaseFields: List<CaseField>): Case {
        return testrail.cases()
            .update(appCase.toTestrailView(), customCaseFields).execute()
    }

    override fun delete(filePath: FilePath) {
        val extractIdFromDeletedFile = ExtractionUtils.extractIdFromDeletedFile(filePath)
        if (extractIdFromDeletedFile != null) {
            testrail.cases().delete(extractIdFromDeletedFile).execute()
        }
    }

    @Suppress("MapGetWithNotNullAssertionOperator", "UnsafeCallOnNullableType")
    override fun move(appCase: AppCase, customCaseFields: List<CaseField>) {
        testrailCaseWebClient.move(appCase.id!!, appCase.sectionId)
        update(appCase, customCaseFields)
    }
}
