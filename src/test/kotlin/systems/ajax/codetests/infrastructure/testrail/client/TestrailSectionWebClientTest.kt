package systems.ajax.codetests.infrastructure.testrail.client

import client.testrail.model.Section
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import kotlin.test.assertEquals

internal class TestrailSectionWebClientTest {

    @Test
    fun `should move section`() {
        // GIVEN
        val sectionId = 1
        val parentId = 2
        val expected = Section()
            .setId(sectionId)
            .setParentId(parentId)

        wireMockServer.stubFor(
            post(urlEqualTo(DEFAULT_BASE_API_PATH + MOVE_SECTION + sectionId))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            objectMapper.writeValueAsString(
                                expected
                            )
                        )
                )

        )

        // WHEN
        val actual = testrailSectionWebClient.move(sectionId, parentId)

        // THEN
        assertEquals(expected, actual)
    }

    companion object {
        lateinit var wireMockServer: WireMockServer
        lateinit var testrailSectionWebClient: TestrailSectionWebClient
        private const val DEFAULT_BASE_API_PATH = "/index.php?/api/v2/"
        private const val MOVE_SECTION = "move_section/"
        private val objectMapper = ObjectMapper()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
            wireMockServer.start()

            val factory = DefaultUriBuilderFactory("http://localhost:${wireMockServer.port()}")
            factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE

            val mockedWebClient = WebClient.builder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .uriBuilderFactory(factory)
                .build()

            testrailSectionWebClient = TestrailSectionWebClient(
                mockedWebClient
            )
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }
    }
}
