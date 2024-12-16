package systems.ajax.codetests.application.port.output

import systems.ajax.codetests.application.model.AppSection
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath

interface SectionRepositoryOutPort {
    fun get(filePath: FilePath): AppSection
    fun writeIdToSectionFile(fileId: FileId)
}
