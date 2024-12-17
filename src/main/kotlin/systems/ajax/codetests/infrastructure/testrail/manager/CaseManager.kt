package systems.ajax.codetests.infrastructure.testrail.manager

import client.testrail.TestRail
import client.testrail.model.Case
import client.testrail.model.CaseField
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.model.diff.Type
import systems.ajax.codetests.application.model.diff.TypeAction
import systems.ajax.codetests.application.port.output.CaseManagerOutPort
import systems.ajax.codetests.infrastructure.testrail.mapper.CaseMapper
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils

@Component
internal class CaseManager(
    private val testrail: TestRail,
) : CaseManagerOutPort {

    override fun publishDiff(differenceMap: Map<TypeAction, List<FilePath>>): List<FileId> {
        val customCaseFields = testrail.caseFields().list().execute()

        val filesToWriteIds = differenceMap[TypeAction(Type.CASE, Action.ADDED)]
            ?.map { add(it.path, customCaseFields) }
            ?.toList().orEmpty()
        differenceMap[TypeAction(Type.CASE, Action.REMOVED)]
            ?.forEach { delete(it.path) }
        differenceMap[TypeAction(Type.CASE, Action.MODIFIED)]
            ?.forEach { update(it.path, customCaseFields) }
        differenceMap[TypeAction(Type.CASE, Action.MOVED_OR_UPDATED)]
            ?.forEach { update(it.path, customCaseFields) }

        return filesToWriteIds
    }

    override fun add(urlToTheFile: String, customCaseFields: List<CaseField>): FileId {
        val newCase = CaseMapper.mapFileToCase(urlToTheFile)
        val response: Case = testrail.cases()
            .add(newCase.sectionId, newCase, customCaseFields).execute()
        return FileId(response.id, FilePath(urlToTheFile))
    }

    private fun update(urlToTheFile: String, customCaseFields: List<CaseField>): Case {
        return testrail.cases()
            .update(CaseMapper.mapFileToCase(urlToTheFile), customCaseFields).execute()
    }

    private fun delete(urlToTheFile: String) {
        val extractIdFromDeletedFile = ExtractionUtils.extractIdFromDeletedFile(FilePath(urlToTheFile))
        if (extractIdFromDeletedFile != null) {
            testrail.cases().delete(extractIdFromDeletedFile).execute()
        }
    }
}
