package systems.ajax.codetests.infrastructure.testrail.client

import client.testrail.model.Section
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
internal class TestrailSectionWebClient(
    private val webClient: WebClient,
) {

    fun move(sectionId: Int, parentId: Int): Section? {
        return webClient.post()
            .uri {
                it.path(DEFAULT_BASE_API_PATH + MOVE_SECTION + sectionId)
                    .build()
            }
            .bodyValue(mapOf("parent_id" to parentId))
            .retrieve()
            .bodyToMono(Section::class.java)
            .block()
    }

    internal companion object {
        private const val DEFAULT_BASE_API_PATH = "/index.php?/api/v2/"
        private const val MOVE_SECTION = "move_section/"
    }
}
