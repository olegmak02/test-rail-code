package systems.ajax.codetests.infrastructure.testrail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "testrail")
data class TestrailProperties(
    val baseUrl: String,
    val login: String,
    val token: String,
)
