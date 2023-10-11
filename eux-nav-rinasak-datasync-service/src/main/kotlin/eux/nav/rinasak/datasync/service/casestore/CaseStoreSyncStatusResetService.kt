package eux.nav.rinasak.datasync.service.casestore

import eux.nav.rinasak.datasync.integration.eux.casestore.EuxCaseStoreClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CaseStoreSyncStatusResetService(
    val euxCaseStoreClient: EuxCaseStoreClient
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreSyncStatusResetService::class.java)

    fun reset() = euxCaseStoreClient.resetSyncStatus()
}
