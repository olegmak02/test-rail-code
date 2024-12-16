package systems.ajax.codetests.application.port.input

import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action

interface ApplyDiffSectionInPort {
    fun execute(filePath: FilePath, action: Action)
}
