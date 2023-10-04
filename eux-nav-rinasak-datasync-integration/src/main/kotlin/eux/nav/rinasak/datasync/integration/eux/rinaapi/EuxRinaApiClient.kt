package eux.nav.rinasak.datasync.integration.eux.rinaapi

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component
class EuxRinaApiClient(
    @Value("\${endpoint.eux.rinaapi}")
    val euxRinaApiUrl: String,
    val euxRinaApiRestTemplate: RestTemplate
) {
    val log: Logger = LoggerFactory.getLogger(EuxRinaApiClient::class.java)

    @OptIn(ExperimentalTime::class)
    fun euxRinaSakOversikt(rinaSakId: Int): EuxRinaSakOversiktV3 {
        val (entity, duration) = measureTimedValue {
            log.info("Henter rinasak: $rinaSakId")
            val entity: ResponseEntity<EuxRinaSakOversiktV3> = euxRinaApiRestTemplate
                .getForEntity("${euxRinaApiUrl}/v3/buc/$rinaSakId/oversikt")
            entity
        }
        log.info("Det tok ${duration.inWholeMilliseconds} milliseconds å hente ut rina oversikt for fnr")
        log.info("hentet fnr ${entity.body!!.fnr} for rinasak $rinaSakId")
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke hente fnr fra eux rina api, status code: ${entity.statusCode}")
        return entity.body!!
    }

    fun fnr(rinaSakId: Int) = euxRinaSakOversikt(rinaSakId).fnr
}
