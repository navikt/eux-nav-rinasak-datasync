package eux.nav.rinasak.datasync.service.util

import eux.nav.rinasak.datasync.integration.eux.casestore.EuxCaseStoreCaseStats
import eux.nav.rinasak.datasync.integration.eux.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.model.Stats
import eux.nav.rinasak.datasync.model.SyncStatus.*
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StatsService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
    val euxCaseStoreClient: EuxCaseStoreClient,
) {
    val log: Logger = LoggerFactory.getLogger(StatsService::class.java)

    fun stats(): Stats {
        val exuCaseStoreStats: EuxCaseStoreCaseStats = try {
            euxCaseStoreClient.stats()
        } catch (e: RuntimeException) {
            log.error("Kunne ikke hente stats fra eux case store", e)
            EuxCaseStoreCaseStats()
        }
        return Stats(
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
            caseStoreRecordRinasakNotFoundCount = caseStoreRecordRepository.countBySyncStatus(RINASAK_NOT_FOUND),
            caseStoreRecordFagsakNotFoundCount = caseStoreRecordRepository.countBySyncStatus(FAGSAK_NOT_FOUND),
            caseStoreRecordInvalidFnrCount = caseStoreRecordRepository.countBySyncStatus(INVALID_FNR),
            inEuxCaseStoreCount = exuCaseStoreStats.numberOfRecords,
            inEuxCaseStorePendingCount = exuCaseStoreStats.numberOfPendingRecords,
            inEuxCaseStoreSyncedCount = exuCaseStoreStats.numberOfSyncedRecords,
            overstyrtEnhetsnummerSyncNullCount = navRinasakRepository
                .countByOverstyrtEnhetsnummerSyncStatusIsNull(),
            overstyrtEnhetsnummerSyncPendingCount = navRinasakRepository
                .countByOverstyrtEnhetsnummerSyncStatus(PENDING),
            overstyrtEnhetsnummerSyncSyncedCount = navRinasakRepository
                .countByOverstyrtEnhetsnummerSyncStatus(SYNCED),
            overstyrtEnhetsnummerSyncFailedCount = navRinasakRepository
                .countByOverstyrtEnhetsnummerSyncStatus(FAILED),
        )
    }
}
