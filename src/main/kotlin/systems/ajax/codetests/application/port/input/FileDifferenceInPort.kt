package systems.ajax.codetests.application.port.input

import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.TypeAction

interface FileDifferenceInPort {
    fun getDifference(): Map<TypeAction, List<FilePath>>
}
