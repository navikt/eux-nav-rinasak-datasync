package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SandboxService(
    val euxRinaApiClient: EuxRinaApiClient,
) {
    val log: Logger = LoggerFactory.getLogger(SandboxService::class.java)

    fun sandbox() {
        val fnr = euxRinaApiClient.fnrOrNull(1444381)
        log.info("fnr: $fnr")
    }
}