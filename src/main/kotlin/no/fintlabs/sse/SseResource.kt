package no.fintlabs.sse

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.sse.Sse
import org.glassfish.jersey.media.sse.EventOutput


@Path("/sse")
class SseResource(
    @Context
    private val sse: Sse
) {

    @GET
    @Path("/connect")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun connect(): EventOutput = EventOutput()

}