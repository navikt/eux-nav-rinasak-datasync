package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.model.Stats
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.model.SyncStatus.SYNCED
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.springframework.stereotype.Service

@Service
class StatsService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
) {
    fun stats() =
        Stats(
            navRinasakCount = navRinasakRepository.count(),
            navRinasakPendingCount = navRinasakRepository.countBySyncStatus(PENDING),
            navRinasakSyncedCount = navRinasakRepository.countBySyncStatus(SYNCED),
            dokumenterCount = dokumentRepository.count(),
            dokumenterPendingCount = dokumentRepository.countBySyncStatus(PENDING),
            dokumenterSyncedCount = dokumentRepository.countBySyncStatus(SYNCED),
            initiellFagsakCount = initiellFagsakRepository.count(),
            initiellFagsakPendingCount = initiellFagsakRepository.countBySyncStatus(PENDING),
            initiellFagsakSyncedCount = initiellFagsakRepository.countBySyncStatus(SYNCED),
            caseStoreRecordCount = caseStoreRecordRepository.count(),
            caseStoreRecordPendingCount = caseStoreRecordRepository.countBySyncStatus(PENDING),
            caseStoreRecordSyncedCount = caseStoreRecordRepository.countBySyncStatus(SYNCED),
        )
}
