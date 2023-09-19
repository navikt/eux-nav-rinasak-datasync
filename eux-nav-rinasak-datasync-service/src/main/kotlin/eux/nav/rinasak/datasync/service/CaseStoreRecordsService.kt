package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreCase
import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import org.springframework.stereotype.Service
import java.util.*

@Service
class CaseStoreRecordsService(
    val euxCaseStoreClient: EuxCaseStoreClient
) {
    fun cases() = euxCaseStoreClient
        .cases()
        ?.map { it.toCaseStoreRecord() }

    fun EuxCaseStoreCase.toCaseStoreRecord() =
        CaseStoreRecord(
            caseStoreRecordUuid = UUID.randomUUID(),
            caseStoreId = id,
            rinasakId = rinaId?.toInt(),
            fagsakNr = navId,
            fagsakTema = theme,
            overstyrtEnhetsnummer = enhetId,
            journalpostId = caseFileId,
            bucId = bucId,
            syncStatus = PENDING
        )
}
