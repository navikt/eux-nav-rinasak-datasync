package eux.nav.rinasak.datasync.integration.navrinasak

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class NavRinasakClient(
    @Value("\${endpoint.eux.navrinasak}")
    val euxNavRinasakUrl: String,
    val euxNavRinasakRestTemplate: RestTemplate
) {

    fun opprettNavRinasak(navRinasakCreateType: NavRinasakCreateType) {
        val request = HttpEntity(navRinasakCreateType)
        val entity: ResponseEntity<Void> = euxNavRinasakRestTemplate
            .postForEntity("${euxNavRinasakUrl}/api/v1/rinasaker", request)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke lagre case: $euxNavRinasakUrl")
    }

    fun finnNavRinasakOrNull(rinasakId: Int) =
        finnNavRinasaker(NavRinasakSearchCriteriaType(rinasakId = rinasakId))
            .navRinasaker
            .firstOrNull()

    fun finnNavRinasaker(
        navRinasakSearchCriteriaType: NavRinasakSearchCriteriaType
    ): NavRinasakSearchResponseType {
        val request = HttpEntity(navRinasakSearchCriteriaType)
        val entity: ResponseEntity<NavRinasakSearchResponseType> = euxNavRinasakRestTemplate
            .postForEntity("${euxNavRinasakUrl}/api/v1/rinasaker/finn", request)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke lagre case: $euxNavRinasakUrl")
        return entity.body!!
    }
}
