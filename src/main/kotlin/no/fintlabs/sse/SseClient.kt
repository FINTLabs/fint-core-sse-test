package no.fintlabs.sse

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.util.retry.Retry
import java.time.Duration

@Component
class SseClient(
    private val client: WebClient,
    private val counter: ConnectionCounter
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        connectWithRetry()
    }

    private fun connectWithRetry() {
        client.get()
            .uri("/sse/connect")
            .accept(TEXT_EVENT_STREAM)
            .exchangeToFlux { response ->
                if (response.statusCode().is2xxSuccessful) {
                    log.info("✔ SSE connected (status={})", response.statusCode())
                    response.bodyToFlux(String::class.java)
                } else {
                    log.error("✘ SSE failed to connect: status={}", response.statusCode())
                    Flux.error(RuntimeException("SSE connection failed with status ${response.statusCode()}"))
                }
            }
            .doOnSubscribe {
                counter.start()
                log.info("→ Timer started for new SSE connection")
            }
            .retryWhen(
                Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(5))
                    .doBeforeRetry { sig ->
                        log.warn(
                            "⚠ SSE disconnected after {}s (cause={}), reconnecting in 5s…",
                            counter.secondsElapsed(),
                            sig.failure().message
                        )
                    }
            )
            .subscribe(
                { data -> log.info("▶ $data") },
                { err -> log.error("✘ Unrecoverable SSE error, giving up", err) }
            )
    }

}

