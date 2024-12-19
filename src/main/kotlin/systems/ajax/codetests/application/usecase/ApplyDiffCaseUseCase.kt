package systems.ajax.codetests.application.usecase

import client.testrail.TestRail
import client.testrail.model.CaseField
import org.springframework.stereotype.Service
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.port.input.ApplyDiffCaseInPort
import systems.ajax.codetests.application.port.output.CaseManagerOutPort
import systems.ajax.codetests.application.port.output.CaseRepositoryOutPort

@Service
internal class ApplyDiffCaseUseCase(
    private val testrail: TestRail,
    private val caseRepository: CaseRepositoryOutPort,
    private val caseManager: CaseManagerOutPort,
) : ApplyDiffCaseInPort {

    override fun execute(filePath: FilePath, action: Action) {
        when (action) {
            Action.ADDED -> caseRepository.get(filePath)
                .let { appCase -> caseManager.add(appCase, getCaseFields()) }
                .let { id -> caseRepository.writeIdToFile(FileId(id, filePath)) }

            Action.REMOVED -> caseManager.delete(filePath)

            Action.MODIFIED -> caseRepository.get(filePath)
                .let { appSection -> caseManager.update(appSection, getCaseFields()) }

            Action.MOVED_OR_UPDATED -> caseRepository.get(filePath)
                .let { appSection -> caseManager.move(appSection, getCaseFields()) }

            Action.UNKNOWN -> error(
                "Something went wrong during processing of this file: $filePath"
            )
        }
    }

    private fun getCaseFields(): List<CaseField> {
        return testrail.caseFields().list().execute()
    }
}
