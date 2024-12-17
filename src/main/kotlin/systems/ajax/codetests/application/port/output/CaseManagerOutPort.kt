package systems.ajax.codetests.application.port.output

import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.TypeAction

interface CaseManagerOutPort {
    fun add(appSection: AppSection): Int
    fun update(appSection: AppSection)
    fun delete(filePath: FilePath)
    fun move(appSection: AppSection)

    fun publishDiff(differenceMap: Map<TypeAction, List<FilePath>>): List<FileId>
}
