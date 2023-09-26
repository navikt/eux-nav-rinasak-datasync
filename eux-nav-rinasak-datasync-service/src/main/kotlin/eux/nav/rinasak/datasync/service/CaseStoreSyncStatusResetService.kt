package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CaseStoreSyncStatusResetService(
    val euxCaseStoreClient: EuxCaseStoreClient,
    val repository: CaseStoreRecordRepository
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreSyncStatusResetService::class.java)

    fun reset() = euxCaseStoreClient.resetSyncStatus()
}
