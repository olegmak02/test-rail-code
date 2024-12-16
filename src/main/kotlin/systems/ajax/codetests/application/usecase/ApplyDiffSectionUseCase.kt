package systems.ajax.codetests.application.usecase

import org.springframework.stereotype.Service
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.port.input.ApplyDiffSectionInPort
import systems.ajax.codetests.application.port.output.SectionManagerOutPort
import systems.ajax.codetests.application.port.output.SectionRepositoryOutPort

@Service
internal class ApplyDiffSectionUseCase(
    private val sectionRepository: SectionRepositoryOutPort,
    private val sectionManager: SectionManagerOutPort,
) : ApplyDiffSectionInPort {

    override fun execute(filePath: FilePath, action: Action) {
        when (action) {
            Action.ADDED ->
                sectionRepository.writeIdToSectionFile(
                    FileId(
                        sectionManager.add(
                            sectionRepository.get(filePath),
                        ),
                        filePath
                    )
                )

            Action.REMOVED -> sectionManager.delete(
                filePath
            )

            Action.MODIFIED -> sectionManager.update(
                sectionRepository.get(
                    filePath
                )
            )

            Action.MOVED_OR_RENAMED -> sectionManager.move(
                sectionRepository.get(
                    filePath
                )
            )

            Action.UNKNOWN -> error(
                "Something went wrong during processing of this file: $filePath"
            )
        }
    }
}
