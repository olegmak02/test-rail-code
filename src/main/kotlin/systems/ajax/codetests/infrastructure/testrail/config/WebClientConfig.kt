package systems.ajax.codetests.infrastructure.testrail.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class WebClientConfig(private val testrailProperties: TestrailProperties) {

    @Bean
    fun webclientNoEncoded(): WebClient {
        val factory = DefaultUriBuilderFactory(testrailProperties.baseUrl)
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE
        return WebClient.builder()
            .uriBuilderFactory(factory)
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .filter(basicAuthentication(testrailProperties.login, testrailProperties.token))
            .build()
    }
}
