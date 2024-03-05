package eux.nav.rinasak.datasync.integration.eux.journal

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
class JournalClient(
    @Value("\${endpoint.journal}")
    val journalUrl: String,
    val euxJournalRestTemplate: RestTemplate
) {

    fun ferdigstill(journalpostId: String) {
        val request = RequestEntity
            .patch(URI("${journalUrl}/api/v1/journalposter/$journalpostId/ferdigstill"))
            .accept(MediaType.APPLICATION_JSON)
            .build()
        euxJournalRestTemplate.exchange(request, Void::class.java);
    }
}
