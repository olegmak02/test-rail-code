package systems.ajax.codetests.application.port.output

import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FilePath

interface SectionManagerOutPort {

    fun add(appSection: AppSection): Int
    fun update(appSection: AppSection)
    fun delete(filePath: FilePath)
    fun move(appSection: AppSection)
}
