package eux.nav.rinasak.datasync.integration.dokarkiv.client

import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivJournalpostOppdatering
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivJournalpostOppdateringRespons
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
class DokarkivClient(
    @Value("\${endpoint.dokarkiv}")
    val dokarkivUrl: String,
    val dokarkivRestTemplate: RestTemplate
) {

    val uri: String by lazy { "$dokarkivUrl/rest/journalpostapi/v1/journalpost" }

    fun oppdater(
        journalpostId: String,
        dokarkivJournalpostOppdatering: DokarkivJournalpostOppdatering
    ) {
        val request: RequestEntity<DokarkivJournalpostOppdatering> = RequestEntity
            .put(URI("$uri/$journalpostId"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(dokarkivJournalpostOppdatering)
        dokarkivRestTemplate.exchange(request, DokarkivJournalpostOppdateringRespons::class.java)
    }
}
