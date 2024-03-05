package eux.nav.rinasak.datasync.integration.dokarkiv.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DokarkivClient(
    @Value("\${endpoint.dokarkiv}")
    val euxDokarkivUrl: String,
    val dokarkivRestTemplate: RestTemplate
) {

}
