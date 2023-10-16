package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import eux.nav.rinasak.datasync.service.casestore.CaseStoreRecordsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.UUID.fromString

@Service
class SendToNavRinasakFacadeService(
    val navRinasakRepository: NavRinasakRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val dokumentRepository: DokumentRepository,
    val integrationService: SendToNavRinasakIntegrationService
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun sync(): Int {
        val navRinasakerPending = navRinasakRepository.findAllBySyncStatus(PENDING)
        val initielleFagsakerPending = initiellFagsakRepository
            .findAllBySyncStatus(PENDING)
            .associateBy { it.navRinasakUuid }
        val dokumenterPending = dokumentRepository
            .findAllBySyncStatus(PENDING)
            .filter { it.gyldigSedId() }
            .map { it.copy(sedId = uuidString(it.sedId)) }
            .filter { it.sedVersjon >= 0 }
            .groupBy { it.navRinasakUuid }
        log.info("Sender ${navRinasakerPending.count()} NAV Rinasaker...")
        navRinasakerPending
            .forEach { integrationService.sync(it, initielleFagsakerPending, dokumenterPending) }
        log.info("Ferdig med oversending av ${navRinasakerPending.count()} NAV Rinasaker!")
        return navRinasakerPending.count()
    }

    fun Dokument.gyldigSedId() =
        try {
            uuid(uuidWithoutDash = sedId)
            true
        } catch (e: RuntimeException) {
            log.error("ugyldig sedId: $sedId", e)
            false
        }

    fun uuid(uuidWithoutDash: String): UUID =
        fromString(uuidString(uuidWithoutDash = uuidWithoutDash))

    fun uuidString(uuidWithoutDash: String): String = with(uuidWithoutDash) {
        listOf(substring(0, 8), substring(8, 12), substring(12, 16), substring(16, 20), substring(20, 32))
            .joinToString(separator = "-")
    }
}
