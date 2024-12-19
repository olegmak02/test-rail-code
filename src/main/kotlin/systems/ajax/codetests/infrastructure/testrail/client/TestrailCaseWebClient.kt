package systems.ajax.codetests.infrastructure.testrail.client

import client.testrail.model.Section
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
internal class TestrailCaseWebClient(
    private val webClient: WebClient,
    @Value("\${testrail.suite.id}") private val suiteId: Int,
) {

    fun move(caseId: Int, sectionId: Int): Section? {
        return webClient.post()
            .uri {
                it.path(DEFAULT_BASE_API_PATH + MOVE_CASE + sectionId)
                    .build()
            }
            .bodyValue(
                mapOf(
                    "suiteId" to suiteId,
                    "case_ids" to listOf(caseId),
                )
            )
            .retrieve()
            .bodyToMono(Section::class.java)
            .block()
    }

    internal companion object {
        private const val DEFAULT_BASE_API_PATH = "/index.php?/api/v2/"
        private const val MOVE_CASE = "move_cases_to_section/"
    }
}
