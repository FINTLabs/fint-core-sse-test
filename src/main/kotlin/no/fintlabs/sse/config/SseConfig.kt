package no.fintlabs.sse.config

import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import org.glassfish.jersey.media.sse.SseFeature
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SseConfig {

    @Bean
    fun jerseyClient(): Client =
        ClientBuilder.newBuilder()
            .register(SseFeature::class.java)
            .build()

}