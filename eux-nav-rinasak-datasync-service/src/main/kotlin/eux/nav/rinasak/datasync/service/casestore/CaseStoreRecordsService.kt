package eux.nav.rinasak.datasync.service.casestore

import eux.nav.rinasak.datasync.integration.eux.casestore.EuxCaseStoreCase
import eux.nav.rinasak.datasync.integration.eux.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.SyncStatus.*
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CaseStoreRecordsService(
    val euxCaseStoreClient: EuxCaseStoreClient,
    val repository: CaseStoreRecordRepository,
    val stagingService: CaseStoreRecordsStagingService
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun cases(): List<CaseStoreRecord> = repository.findAll()

    fun casesFagsakNotFound(): List<CaseStoreRecord> = repository.findAllBySyncStatus(FAGSAK_NOT_FOUND)

    fun casesInvalidFnr(): List<CaseStoreRecord> = repository.findAllBySyncStatus(INVALID_FNR)

    fun casesRinasakNotFound(): List<CaseStoreRecord> = repository.findAllBySyncStatus(RINASAK_NOT_FOUND)

    fun EuxCaseStoreCase.toCaseStoreRecord() =
        CaseStoreRecord(
            caseStoreRecordUuid = UUID.randomUUID(),
            caseStoreId = id,
            rinasakId = if (rinaId.isNullOrEmpty()) 0 else rinaId!!.toInt(),
            fagsakId = navId,
            fagsakTema = theme,
            overstyrtEnhetsnummer = enhetId,
            journalpostId = caseFileId,
            bucId = bucId,
            syncStatus = PENDING
        )

    fun populateAll(): Int {
        val next = populateNext()
        return if (next > 0) {
            log.info("Fant $next records i case store...")
            populateAll() + next
        } else {
            log.info("Fant ikke flere records i case store")
            0
        }
    }

    fun populateNext() = euxCaseStoreClient
        .nextCases()
        .map { euxCaseStoreClient.save(it.copy(syncStatus = "SYNCED")) }
        .map { it.toCaseStoreRecord() }
        .map { repository.save(it) }
        .size

    fun resetRecords() = repository.deleteAll()

    fun populateNavRinasakStaging(): Int {
        val caseStoreRecordsByRinasak = repository
            .findAllBySyncStatus(PENDING)
            .also { log.info("${it.size} case store records pending sync") }
            .groupBy { it.rinasakId }
        caseStoreRecordsWithOneEntry(caseStoreRecordsByRinasak)
            .also {
                stageCaseStoreRecordsWithOneEntryMissingJournalpostId(it)
                stageCaseStoreRecordsWithOneEntryAndJournalpostId(it)
            }
        caseStoreRecordsWithMoreThanOneEntry(caseStoreRecordsByRinasak)
            .also {
                stageCaseStoreRecordsWithMoreThanOneEntryMissingJournalpost(it)
                stageCaseStoreRecordsWithMoreThanOneEntryWithJournalpost(it)
            }
        return caseStoreRecordsByRinasak.size
    }

    fun caseStoreRecordsWithMoreThanOneEntry(
        caseStoreRecordsByRinasak: Map<Int, List<CaseStoreRecord>>
    ) =
        caseStoreRecordsByRinasak
            .filter { it.value.size > 1 }
            .also { log.info("${it.size} rina cases in case store with more than 1 record") }

    fun caseStoreRecordsWithOneEntry(
        caseStoreRecordsByRinasak: Map<Int, List<CaseStoreRecord>>
    ) =
        caseStoreRecordsByRinasak
            .filter { it.value.size == 1 }
            .also { log.info("${it.size} rina cases in case store with 1 record") }
            .mapValues { it.value.first() }

    fun stageCaseStoreRecordsWithOneEntryMissingJournalpostId(
        caseStoreRecordsWithOneEntry: Map<Int, CaseStoreRecord>
    ) =
        caseStoreRecordsWithOneEntry
            .filter { it.value.journalpostId.isNullOrEmpty() }
            .also { log.info("${it.size} rina cases in case store with 1 record and no journalpostId") }
            .forEach { stageCaseStoreRecordWithOneEntryMissingJournalpostId(it.key, it.value) }

    fun stageCaseStoreRecordsWithOneEntryAndJournalpostId(
        caseStoreRecordsWithOneEntry: Map<Int, CaseStoreRecord>
    ) =
        caseStoreRecordsWithOneEntry
            .filterNot { it.value.journalpostId.isNullOrEmpty() }
            .also { log.info("${it.size} rina cases in case store with 1 record and journalpostId") }
            .forEach { stagingService.stageCaseStoreRecordWithOneEntryAndJournalpostId(it.key, it.value) }

    fun stageCaseStoreRecordsWithMoreThanOneEntryWithJournalpost(
        caseStoreRecordsWithMoreThanOneEntry: Map<Int, List<CaseStoreRecord>>
    ) =
        caseStoreRecordsWithMoreThanOneEntry
            .filter { entry -> entry.value.any { !it.journalpostId.isNullOrEmpty() } }
            .also { log.info("${it.size} rina cases in case store with more than 1 record and journalpost") }
            .forEach { stagingService.stageCaseStoreRecordsWithMoreThanOneEntryWithJournalposts(it.key, it.value) }

    fun stageCaseStoreRecordsWithMoreThanOneEntryMissingJournalpost(
        caseStoreRecordsWithMoreThanOneEntry: Map<Int, List<CaseStoreRecord>>
    ) =
        caseStoreRecordsWithMoreThanOneEntry
            .filter { entry -> entry.value.all { it.journalpostId.isNullOrEmpty() } }
            .also { log.info("${it.size} rina cases in case store with more than 1 record and no journalpost") }
            .forEach { stageCaseStoreRecordWithMoreThanOneEntryMissingJournalpost(it.key, it.value) }

    fun stageCaseStoreRecordWithMoreThanOneEntryMissingJournalpost(
        rinasakId: Int,
        records: List<CaseStoreRecord>
    ) {
        val fagsakId = records
            .map { it.fagsakId }
            .firstOrNull()
        if (fagsakId != null)
            stagingService.stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId, records)
        else
            log.info("Rinasak $rinasakId har ikke fagsakId eller journalpostId og mer enn 1 case store record")
    }

    fun stageCaseStoreRecordWithOneEntryMissingJournalpostId(
        rinasakId: Int,
        record: CaseStoreRecord
    ) {
        val fagsakId = record.fagsakId
        if (fagsakId != null)
            stagingService.stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId, record)
        else
            log.info("Rinasak $rinasakId har ikke fagsakId eller journalpostId og 1 case store record")
    }
}
