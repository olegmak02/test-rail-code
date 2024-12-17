package systems.ajax.codetests.infrastructure.testrail.mapper

import client.testrail.model.Section
import systems.ajax.codetests.application.model.AppSection

object SectionMapper {

    fun AppSection.toTestrailView(): Section {
        val section = Section()
        id?.let { section.id = it }
        section
            .setName(name)
            .setParentId(parentId)
            .setDescription(description)
        return section
    }

    fun Section.toAppView(): AppSection {
        return AppSection(
            id,
            name,
            description,
            parentId,
        )
    }
}
