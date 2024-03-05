package eux.nav.rinasak.datasync.integration.eux.journal

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.patchForObject

@Component
class JournalClient(
    @Value("\${endpoint.journal}")
    val journalUrl: String,
    val euxJournalRestTemplate: RestTemplate
) {

    fun ferdigstill(journalpostId: String) {
        val entity: ResponseEntity<Void> = euxJournalRestTemplate
            .patchForObject("${journalUrl}/api/v1/journalposter/$journalpostId/ferdigstill")
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke ferdigstille: $journalUrl")
    }

}
