package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ResetNavRinasakDatasyncService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
) {
    val log: Logger = LoggerFactory.getLogger(ResetNavRinasakDatasyncService::class.java)

    fun reset() {
        dokumentRepository.deleteAll()
        initiellFagsakRepository.deleteAll()
        navRinasakRepository.deleteAll()
        try {
            caseStoreRecordRepository.resetSyncStatus()
        } catch (e: RuntimeException) {
            log.error("Feilet under resetting av sync status for case store records", e)
        }
        log.info("Nav rinasak datasync resatt")
    }
}