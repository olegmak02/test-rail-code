package systems.ajax.codetests.infrastructure.testrail.mapper

import client.testrail.model.Case
import client.testrail.model.Field
import systems.ajax.codetests.application.model.appcase.AppCase
import systems.ajax.codetests.application.model.appcase.AppStep
import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.PRECONDITIONS
import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.STEPS

internal object CaseMapper {
    fun AppCase.toTestrailView(): Case {
        val case = Case()
        id?.let { case.id = it }
        case
            .setTitle(title)
            .setSectionId(sectionId)
            .setTypeId(type?.ordinal)
            .setPriorityId(priority?.ordinal)
            .setRefs(refs)
            .setEstimate(estimate)
            .addCustomField(STEPS.value, steps.map { it.toTestrailView() }.ifEmpty { null })
            .addCustomField(PRECONDITIONS.value, precondition)
        return case
    }

    fun AppStep.toTestrailView(): Field.Step {
        val step = Field.Step()
        step.content = content
        step.expected = expected
        return step
    }
}
