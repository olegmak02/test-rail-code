package systems.ajax.codetests.infrastructure.gitdifference

import org.springframework.stereotype.Component
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.model.diff.Type
import systems.ajax.codetests.application.model.diff.TypeAction
import systems.ajax.codetests.application.port.input.FileDifferenceInPort
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ProcessBuilder

@Component
internal class GitDifference : FileDifferenceInPort {

    override fun getDifference(): Map<TypeAction, List<FilePath>> {
        return executeGitDiffCommand()
            .split(System.lineSeparator())
            .filter { it.contains(CASE_EXTENSION) || it.contains(SECTION_EXTENSION) }
            .filter { it.contains(RESOURCES_FOLDER) }
            .map { line ->
                if (line.contains(CASE_EXTENSION)) {
                    extractAction(Type.CASE, line.split(trailingSpaceRegex))
                } else {
                    extractAction(Type.SECTION, line.split(trailingSpaceRegex))
                }
            }.groupBy(
                { it.first },
                { it.second }
            )
    }

    private fun extractAction(type: Type, parts: List<String>): Pair<TypeAction, FilePath> {
        return if (parts[0].contains(GIT_MOVED_OR_UPDATED_SYMBOL)) {
            TypeAction(type, Action.MOVED_OR_UPDATED) to FilePath(parts[2])
        } else if (parts[0] == GIT_REMOVED_SYMBOL) {
            TypeAction(type, Action.REMOVED) to FilePath(parts[1])
        } else if (parts[0] == GIT_MODIFIED_SYMBOL) {
            TypeAction(type, Action.MODIFIED) to FilePath(parts[1])
        } else if (parts[0] == GIT_ADDED_SYMBOL) {
            TypeAction(type, Action.ADDED) to FilePath(parts[1])
        } else {
            TypeAction(type, Action.UNKNOWN) to FilePath(parts[1])
        }
    }

    private fun executeGitDiffCommand(): String {
        val process = ProcessBuilder("git", "diff", "--name-status", "--find-renames", "main").start()
        return BufferedReader(InputStreamReader(process.inputStream)).readText()
    }

    private companion object {
        private val trailingSpaceRegex = Regex("\\s+")
        private const val SECTION_EXTENSION = ".section"
        private const val RESOURCES_FOLDER = "resources"
        private const val CASE_EXTENSION = ".feature"
        private const val GIT_REMOVED_SYMBOL = "D"
        private const val GIT_MODIFIED_SYMBOL = "M"
        private const val GIT_ADDED_SYMBOL = "A"
        private const val GIT_MOVED_OR_UPDATED_SYMBOL = "R"
    }
}
