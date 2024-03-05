package eux.nav.rinasak.datasync.integration.dokarkiv.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NavRinasakClient(
    @Value("\${endpoint.navrinasak}")
    val euxNavRinasakUrl: String,
    val euxNavRinasakRestTemplate: RestTemplate
) {

}
