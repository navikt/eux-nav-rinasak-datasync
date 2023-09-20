package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreCase
import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CaseStoreRecordsService(
    val euxCaseStoreClient: EuxCaseStoreClient,
    val repository: CaseStoreRecordRepository
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun cases(): List<CaseStoreRecord> = repository.findAll()

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

    fun populateNext() = euxCaseStoreClient
        .nextCases()
        .map { it.toCaseStoreRecord() }
        .also { log.info("next size: ${it.size}") }
        .map { repository.save(it) }
        .size
}
