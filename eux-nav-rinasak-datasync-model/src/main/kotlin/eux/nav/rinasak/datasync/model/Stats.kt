package eux.nav.rinasak.datasync.model

data class Stats(
    val navRinasakCount: Long,
    val dokumenterCount: Long,
    val initiellFagsakCount: Long,
    val caseStoreRecordCount: Long,
    val caseStoreRecordPendingCount: Long,
    val caseStoreRecordSyncedCount: Long,
)
