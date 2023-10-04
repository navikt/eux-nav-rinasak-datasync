package eux.nav.rinasak.datasync.integration.eux.casestore

data class EuxCaseStoreCaseStats(
    var numberOfRecords: Long = 0,
    var numberOfPendingRecords: Long = 0,
    var numberOfSyncedRecords: Long = 0
)
