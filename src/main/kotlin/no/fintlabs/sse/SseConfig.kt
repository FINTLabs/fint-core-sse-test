package no.fintlabs.sse

import org.glassfish.jersey.media.sse.SseFeature
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SseConfig {

    @Bean
    fun jerseyConfig(): ResourceConfig =
        ResourceConfig()
            .register(SseFeature::class.java)
            .register(SseResource::class.java)

}