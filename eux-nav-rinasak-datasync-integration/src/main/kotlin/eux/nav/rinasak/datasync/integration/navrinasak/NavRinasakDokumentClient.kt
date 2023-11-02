package eux.nav.rinasak.datasync.integration.navrinasak

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class NavRinasakDokumentClient(
    @Value("\${endpoint.navrinasak}")
    val euxNavRinasakUrl: String,
    val euxNavRinasakRestTemplate: RestTemplate
) {

    fun opprettNavRinasakDokument(rinasakId: Int, dokumentCreateType: DokumentCreateType) {
        val request = HttpEntity(dokumentCreateType)
        val entity: ResponseEntity<Void> = euxNavRinasakRestTemplate
            .postForEntity("${euxNavRinasakUrl}/api/v1/rinasaker/$rinasakId/dokumenter", request)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke lagre dokument: $euxNavRinasakUrl")
    }
}
