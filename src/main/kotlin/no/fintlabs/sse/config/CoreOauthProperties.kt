package no.fintlabs.sse.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fint.core.oauth2")
data class CoreOauthProperties(
    val username: String,
    val password: String,
    val clientId: String,
    val clientSecret: String,
    val scope: String = "fint-client",
    val tokenUri: String = "https://idp.felleskomponent.no/nidp/oauth/nam/token",
    val registrationId: String = "fint"
)