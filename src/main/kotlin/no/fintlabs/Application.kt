package no.fintlabs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FintCoreSseTestServerApplication

fun main(args: Array<String>) {
    runApplication<FintCoreSseTestServerApplication>(*args)
}
