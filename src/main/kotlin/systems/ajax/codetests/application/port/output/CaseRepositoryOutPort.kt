package systems.ajax.codetests.application.port.output

import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.appcase.AppCase

interface CaseRepositoryOutPort {
    fun get(filePath: FilePath): AppCase
    fun writeIdToFile(fileId: FileId)
}
