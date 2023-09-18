package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreClient
import org.springframework.stereotype.Service

@Service
class CaseStoreRecordsService(
    val euxCaseStoreClient: EuxCaseStoreClient
) {
    fun cases() = euxCaseStoreClient.cases()
}