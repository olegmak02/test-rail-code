package systems.ajax.codetests.application.usecase

// @Service
// internal class ApplyDiffCaseUseCase(
// //    private val caseRepository: CaseRepositoryOutPort,
//    private val caseManager: CaseManagerOutPort,
// ) : ApplyDiffSectionInPort {
//
//    override fun execute(filePath: FilePath, action: Action) {
//        when (action) {
//            Action.ADDED -> caseRepository.get(filePath)
//                .let { appSection -> caseManager.add(appSection) }
//                .let { id -> sectionRepository.writeIdToSectionFile(FileId(id, filePath)) }
//
//            Action.REMOVED -> caseManager.delete(filePath)
//
//            Action.MODIFIED -> sectionRepository.get(filePath)
//                .let { appSection -> caseManager.update(appSection) }
//
//            Action.MOVED_OR_UPDATED -> sectionRepository.get(filePath)
//                .let { appSection -> caseManager.move(appSection) }
//
//            Action.UNKNOWN -> error(
//                "Something went wrong during processing of this file: $filePath"
//            )
//        }
//    }
// }
