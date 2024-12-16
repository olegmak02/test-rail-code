package systems.ajax.codetests.infrastructure.testrail.config

import client.testrail.TestRail
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TestrailProperties::class)
class TestrailConfig(private val testrailProperties: TestrailProperties) {

    @Bean
    fun testrailClient(): TestRail {
        return TestRail.builder(
            testrailProperties.baseUrl,
            testrailProperties.login,
            testrailProperties.token,
        ).build()
    }
}
