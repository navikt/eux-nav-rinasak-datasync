package eux.nav.rinasak.datasync.integration.casestore

data class EuxCaseStoreCaseStats(
    var numberOfRecords: Long,
    var numberOfPendingRecords: Long,
    var numberOfSyncedRecords: Long
)
