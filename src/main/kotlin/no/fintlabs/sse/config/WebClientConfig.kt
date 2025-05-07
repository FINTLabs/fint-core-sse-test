package no.fintlabs.sse.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.Map
import java.util.function.Function


@Configuration
class WebClientConfig(
    private val oauthProperties: CoreOauthProperties,
    private val sseProperties: SseProperties
) {

    @Bean
    fun oauth2Filter(authManager: ReactiveOAuth2AuthorizedClientManager): ServerOAuth2AuthorizedClientExchangeFilterFunction {
        val test = ServerOAuth2AuthorizedClientExchangeFilterFunction(authManager)
        test.setDefaultClientRegistrationId(oauthProperties.registrationId)
        return test
    }

    @Bean
    fun reactiveWebClient(
        oauth2Filter: ServerOAuth2AuthorizedClientExchangeFilterFunction,
        sseProperties: SseProperties
    ): WebClient =
        WebClient.builder()
            .baseUrl(sseProperties.url)
            .filter(oauth2Filter)
            .build()
}
