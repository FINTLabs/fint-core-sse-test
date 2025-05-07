package no.fintlabs.sse.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fint.sse")
data class SseProperties(
    val url: String
)