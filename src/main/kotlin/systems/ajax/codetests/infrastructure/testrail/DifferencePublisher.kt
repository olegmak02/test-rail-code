package systems.ajax.codetests.infrastructure.testrail

import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.diff.Type
import systems.ajax.codetests.application.port.input.ApplyDiffSectionInPort
import systems.ajax.codetests.application.port.input.FileDifferenceInPort
import systems.ajax.codetests.application.port.output.TestrailManagerOutPort

@Component
internal class DifferencePublisher(
    private val fileDifferenceInPort: FileDifferenceInPort,
    private val applyDiffSectionInPort: ApplyDiffSectionInPort,
) : TestrailManagerOutPort {

    override fun publish() {
        fileDifferenceInPort.getDifference()
            .forEach { (typeAction, files) ->
                when (typeAction.type) {
                    Type.CASE -> TODO()
                    Type.SECTION -> files.forEach {
                        applyDiffSectionInPort.execute(it, typeAction.action)
                    }
                }
            }
    }
}
