package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakClient
import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NavRinasakService(
    val navRinasakClient: NavRinasakClient
) {

    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun save(navRinasak: NavRinasak) {
        log.info("saving nav rinasak: ${navRinasak.rinasakId} : ${navRinasak.navRinasakUuid}")
    }

    fun save(initiellFagsak: InitiellFagsak) {
        log.info("legger til initiell fagsak ${initiellFagsak.id} på: ${initiellFagsak.navRinasakUuid}")
    }

    fun save(dokument: Dokument) {
        log.info("legger til dokument ${dokument.dokumentInfoId} på: ${dokument.navRinasakUuid}")
    }
}