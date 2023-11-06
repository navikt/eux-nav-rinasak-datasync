package eux.nav.rinasak.datasync.model

data class Stats(
    val navRinasakCount: Long,
    val navRinasakPendingCount: Long,
    val navRinasakSyncedCount: Long,
    val dokumenterCount: Long,
    val dokumenterPendingCount: Long,
    val dokumenterSyncedCount: Long,
    val initiellFagsakCount: Long,
    val initiellFagsakPendingCount: Long,
    val initiellFagsakSyncedCount: Long,
    val caseStoreRecordCount: Long,
    val caseStoreRecordPendingCount: Long,
    val caseStoreRecordSyncedCount: Long,
    val caseStoreRecordRinasakNotFoundCount: Long,
    val caseStoreRecordFagsakNotFoundCount: Long,
    val inEuxCaseStoreCount: Long,
    val inEuxCaseStorePendingCount: Long,
    val inEuxCaseStoreSyncedCount: Long,
    val caseStoreRecordInvalidFnrCount: Long,
)
