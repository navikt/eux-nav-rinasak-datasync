package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResetNavRinasakDatasyncService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
) {
    val log = logger {}

    @Transactional
    fun reset() {
        dokumentRepository.deleteAll()
        initiellFagsakRepository.deleteAll()
        navRinasakRepository.deleteAll()
        try {
            caseStoreRecordRepository.resetSyncStatus()
        } catch (e: RuntimeException) {
            log.error(e) { "Feilet under resetting av sync status for case store records" }
        }
        log.info { "Nav rinasak datasync resatt" }
    }
}
