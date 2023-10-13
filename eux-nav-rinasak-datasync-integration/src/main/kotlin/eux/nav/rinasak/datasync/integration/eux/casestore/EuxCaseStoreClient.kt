package eux.nav.rinasak.datasync.integration.eux.casestore

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.GET
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class EuxCaseStoreClient(
    @Value("\${endpoint.casestore}")
    val euxCaseStoreUrl: String,
    val euxCaseStoreRestTemplate: RestTemplate
) {

    fun save(euxCaseStoreCase: EuxCaseStoreCase): EuxCaseStoreCase {
        val request = HttpEntity(euxCaseStoreCase)
        val entity: ResponseEntity<EuxCaseStoreCase> = euxCaseStoreRestTemplate
            .postForEntity("${euxCaseStoreUrl}/cases", request)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke lagre case: $euxCaseStoreCase")
        return entity.body!!
    }

    fun nextCases() =
        euxCaseStoreRestTemplate
            .also { println("${euxCaseStoreUrl}/cases/next") }
            .exchange(
                "${euxCaseStoreUrl}/cases/next",
                GET, null, object : ParameterizedTypeReference<List<EuxCaseStoreCase>>() {}
            )
            .body!!

    fun resetSyncStatus() {
        val entity: ResponseEntity<Void> = euxCaseStoreRestTemplate
            .postForEntity("${euxCaseStoreUrl}/cases/resetsyncstatus", Void::class.java)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke resette sync status")
    }

    fun stats(): EuxCaseStoreCaseStats {
        val entity: ResponseEntity<EuxCaseStoreCaseStats> = euxCaseStoreRestTemplate
            .getForEntity("${euxCaseStoreUrl}/cases/stats", EuxCaseStoreCaseStats::class.java)
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke hente stats, status code: ${entity.statusCode}")
        return entity.body!!
    }
}
