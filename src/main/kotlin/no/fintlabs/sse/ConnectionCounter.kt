package no.fintlabs.sse

import org.springframework.stereotype.Component

@Component
class ConnectionCounter {

    @Volatile
    private var startTimeMillis: Long = 0

    fun start() {
        startTimeMillis = System.currentTimeMillis()
    }

    fun secondsElapsed(): Long {
        val now = System.currentTimeMillis()
        return (now - startTimeMillis) / 1_000
    }
}