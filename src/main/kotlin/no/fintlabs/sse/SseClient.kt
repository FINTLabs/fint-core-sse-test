package no.fintlabs.sse

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.retry.Retry
import java.time.Duration

@Component
class SseClient(
    private val client: WebClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        client.get()
            .uri("/sse/connect")
            .accept(TEXT_EVENT_STREAM)
            .exchangeToFlux { response ->
                handleResponseLogging(response)
                response.bodyToFlux(String::class.java)
            }
            .retryWhen(
                Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(5))
                    .doBeforeRetry { sig ->
                        log.warn("SSE disconnected (cause=${sig.failure().message}), reconnecting…")
                    }
            )
            .subscribe(
                { data -> log.info("▶ $data") },
                { err -> log.error("⚠ SSE stream error", err) }
            )
    }

    private fun handleResponseLogging(response: ClientResponse) {
        if (response.statusCode().is2xxSuccessful) {
            log.info("✔ SSE connected (status={})", response.statusCode())
        } else {
            log.error("✘ SSE failed to connect: status={}", response.statusCode())
        }
    }

}

