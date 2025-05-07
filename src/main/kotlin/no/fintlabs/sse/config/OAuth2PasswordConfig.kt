package no.fintlabs.sse.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import reactor.core.publisher.Mono


@Configuration
class OAuth2PasswordConfig(
    private val coreOauthProperties: CoreOauthProperties
) {

    @Bean
    fun reactiveClientRegistrationRepository(): ReactiveClientRegistrationRepository {
        val registration = ClientRegistration.withRegistrationId(coreOauthProperties.registrationId)
            .authorizationGrantType(AuthorizationGrantType.PASSWORD)
            .tokenUri(coreOauthProperties.tokenUri)
            .clientId(coreOauthProperties.clientId)
            .clientSecret(coreOauthProperties.clientSecret)
            .scope(coreOauthProperties.scope)
            .build()

        return InMemoryReactiveClientRegistrationRepository(registration)
    }

    @Bean
    fun serverOAuth2AuthorizedClientRepository(): ServerOAuth2AuthorizedClientRepository =
        WebSessionServerOAuth2AuthorizedClientRepository()

    @Bean
    fun reactiveOAuth2AuthorizedClientService(
        clientRegs: ReactiveClientRegistrationRepository
    ): ReactiveOAuth2AuthorizedClientService =
        InMemoryReactiveOAuth2AuthorizedClientService(clientRegs)

    @Bean
    fun reactiveAuthorizedClientManager(
        clientRegistrations: ReactiveClientRegistrationRepository,
        authClientService: ReactiveOAuth2AuthorizedClientService
    ): ReactiveOAuth2AuthorizedClientManager {
        val provider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .password()
            .refreshToken()
            .build()

        val manager = AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrations, authClientService
        )

        manager.setAuthorizedClientProvider(provider)
        manager.setContextAttributesMapper {
            Mono.just(
                mapOf(
                    OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME to coreOauthProperties.username,
                    OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME to coreOauthProperties.password
                )
            )
        }

        return manager
    }

}