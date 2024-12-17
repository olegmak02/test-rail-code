package systems.ajax.codetests.infrastructure.gitdifference

import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.diff.Action
import systems.ajax.codetests.application.model.diff.Type
import systems.ajax.codetests.application.model.diff.TypeAction
import java.util.stream.Stream
import kotlin.test.assertEquals

internal class GitDifferenceTest {

    @ParameterizedTest
    @MethodSource("arguments for should test getDiff parses git diff output")
    fun `test getDiff parses git diff output`(input: String, expectedResult: Pair<TypeAction, FilePath>) {
        // GIVEN
        val gitDifference = spyk(GitDifference())
        every { gitDifference["executeGitDiffCommand"]() } returns input
        val expected: Map<TypeAction, List<FilePath>> = listOf(
            expectedResult
        ).groupBy({ it.first }, { it.second })

        // WHEN
        val result: Map<TypeAction, List<FilePath>> = gitDifference.getDifference()

        // THEN
        assertEquals(expected, result)
    }

    @Suppress("LongMethod")
    private companion object {

        @JvmStatic
        fun `arguments for should test getDiff parses git diff output`(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "R100    us-subdomain/resources/folder/util/LogginExtention.section   " +
                        "   us-subdomain/resources/folder/util2/LogginExtention.section",
                    TypeAction(Type.SECTION, Action.MOVED_OR_UPDATED)
                        to
                        FilePath("us-subdomain/resources/folder/util2/LogginExtention.section")
                ),
                Arguments.of(
                    "R045    us-subdomain/resources/LogginExtention.section      " +
                        "us-subdomain/resources/util/LogginExtention.section",
                    TypeAction(Type.SECTION, Action.MOVED_OR_UPDATED)
                        to
                        FilePath("us-subdomain/resources/util/LogginExtention.section")
                ),
                Arguments.of(
                    "A       us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.section",
                    TypeAction(Type.SECTION, Action.ADDED)
                        to
                        FilePath("us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.section")
                ),
                Arguments.of(
                    "D       us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.section",
                    TypeAction(Type.SECTION, Action.REMOVED)
                        to
                        FilePath("us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.section")
                ),
                Arguments.of(
                    "M      us-subdomain/resources/sms/src/test/kotlin/LoggingEventListenerTest.section",
                    TypeAction(Type.SECTION, Action.MODIFIED)
                        to
                        FilePath("us-subdomain/resources/sms/src/test/kotlin/LoggingEventListenerTest.section")
                ),
                Arguments.of(
                    "R100    us-subdomain/resources/folder/util/LogginExtention.feature   " +
                        "   us-subdomain/resources/folder/util2/LogginExtention.feature",
                    TypeAction(Type.CASE, Action.MOVED_OR_UPDATED)
                        to
                        FilePath("us-subdomain/resources/folder/util2/LogginExtention.feature")
                ),
                Arguments.of(
                    "R100    us-subdomain/resources/LogginExtentionN.feature      " +
                        "us-subdomain/resources/LogginExtentionN.feature",
                    TypeAction(Type.CASE, Action.MOVED_OR_UPDATED)
                        to
                        FilePath("us-subdomain/resources/LogginExtentionN.feature")
                ),
                Arguments.of(
                    "R045    us-subdomain/resources/LogginExtention.feature    " +
                        "  us-subdomain/resources/util/LogginExtention.feature",
                    TypeAction(Type.CASE, Action.MOVED_OR_UPDATED)
                        to
                        FilePath("us-subdomain/resources/util/LogginExtention.feature")
                ),
                Arguments.of(
                    "A       us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature",
                    TypeAction(Type.CASE, Action.ADDED)
                        to
                        FilePath("us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature")
                ),
                Arguments.of(
                    "D       us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature",
                    TypeAction(Type.CASE, Action.REMOVED)
                        to
                        FilePath("us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature")
                ),
                Arguments.of(
                    "M       us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature",
                    TypeAction(Type.CASE, Action.MODIFIED)
                        to
                        FilePath("us-subdomain/resources/sms/src/main/kotlin/sms/listener/LoggingEventListener.feature")
                )
            )
        }
    }
}
