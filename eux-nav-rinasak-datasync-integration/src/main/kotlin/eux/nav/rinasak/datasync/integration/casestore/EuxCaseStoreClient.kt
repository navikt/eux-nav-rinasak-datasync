package eux.nav.rinasak.datasync.integration.casestore

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class EuxCaseStoreClient(
    @Value("\${endpoint.eux.casestore}")
    val euxCaseStoreUrl: String,
    val euxCaseStoreRestTemplate: RestTemplate
) {

    fun cases() =
        euxCaseStoreRestTemplate
            .also { println("${euxCaseStoreUrl}/cases/next") }
            .exchange(
                "${euxCaseStoreUrl}/cases/next",
                HttpMethod.GET, null, object : ParameterizedTypeReference<List<EuxCaseStoreCase>>() {}
            )
            .body
}
