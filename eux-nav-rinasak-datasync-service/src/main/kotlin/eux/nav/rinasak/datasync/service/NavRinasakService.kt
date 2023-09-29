package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NavRinasakService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
) {

    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun save(navRinasak: NavRinasak) {
        val existingNavRinasak = navRinasakRepository.findByIdOrNull(navRinasak.navRinasakUuid)
        if (existingNavRinasak == null) {
            log.info("saving nav rinasak: ${navRinasak.rinasakId} : ${navRinasak.navRinasakUuid}")
            navRinasakRepository.save(navRinasak)
        } else {
            log.info("rinasak $navRinasak finnes alt, skipping")
        }
    }

    fun save(initiellFagsak: InitiellFagsak) {
        val existingInitiellFagsak = initiellFagsakRepository.findByIdOrNull(initiellFagsak.navRinasakUuid)
        if (existingInitiellFagsak == null) {
            log.info("legger til initiell fagsak ${initiellFagsak.id} på: ${initiellFagsak.navRinasakUuid}")
            initiellFagsakRepository.save(initiellFagsak)
        } else {
            log.info("rinasak ${initiellFagsak.navRinasakUuid} har alt initiell fagsak, skipping")
        }
    }

    fun save(dokument: Dokument) {
        log.info("legger til dokument ${dokument.dokumentInfoId} på: ${dokument.navRinasakUuid}")
        dokumentRepository.save(dokument)
    }
}