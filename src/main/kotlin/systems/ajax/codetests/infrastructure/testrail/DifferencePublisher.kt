package systems.ajax.codetests.infrastructure.testrail

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.diff.Type
import systems.ajax.codetests.application.port.input.ApplyDiffCaseInPort
import systems.ajax.codetests.application.port.input.ApplyDiffSectionInPort
import systems.ajax.codetests.application.port.input.FileDifferenceInPort
import systems.ajax.codetests.application.port.output.TestrailManagerOutPort

@Component
internal class DifferencePublisher(
    private val fileDifferenceInPort: FileDifferenceInPort,
    private val applyDiffSectionInPort: ApplyDiffSectionInPort,
    private val applyDiffCaseInPort: ApplyDiffCaseInPort,
) : TestrailManagerOutPort {

    @EventListener(ApplicationReadyEvent::class)
    override fun publish() {
        fileDifferenceInPort.getDifference()
            .entries.sortedBy { it.key.type.ordinal }.reversed()
            .forEach { (typeAction, files) ->
                when (typeAction.type) {
                    Type.CASE -> files.forEach {
                        applyDiffCaseInPort.execute(it, typeAction.action)
                    }

                    Type.SECTION -> files.forEach {
                        applyDiffSectionInPort.execute(it, typeAction.action)
                    }
                }
            }
    }
}
