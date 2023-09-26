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

    fun populateAll(): Int {
        val next = populateNext()
        return if (next > 0){
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
        .also { log.info("next size: ${it.size}") }
        .map { repository.save(it) }
        .size

    fun populateNavRinasakStaging(): Int {
        val caseStoreRecordsByRinasak = repository
            .findAll()
            .groupBy { it.rinasakId }
        val caseStoreRecordsWithOneEntry = caseStoreRecordsByRinasak
            .filter { it.value.size == 1 }
            .also { log.info("${it.size} rina cases in case store with 1 record") }
            .mapValues { it.value.first() }
        val caseStoreRecordsWithOneEntryMissingJournalpostId = caseStoreRecordsWithOneEntry
            .filter { it.value.journalpostId.isNullOrEmpty() }
            .also { log.info("${it.size} rina cases in case store with 1 record and no journalpostId") }
        val caseStoreRecordsWithOneEntryAndJournalpostId = caseStoreRecordsWithOneEntry
            .filter { it.value.journalpostId.isNullOrEmpty() }
            .also { log.info("${it.size} rina cases in case store with 1 record and journalpostId") }
        val caseStoreRecordsWithMoreThanOneEntry = caseStoreRecordsByRinasak
            .filter { it.value.size > 1 }
            .also { log.info("${it.size} rina cases in case store with more than 1 record") }
        val caseStoreRecordsWithMoreThanOneEntryWithJournalpost = caseStoreRecordsWithMoreThanOneEntry
            .filter { entry -> entry.value.any { !it.journalpostId.isNullOrEmpty() } }
            .also { log.info("${it.size} rina cases in case store with more than 1 record and journalpost") }
        val caseStoreRecordsWithMoreThanOneEntryMissingJournalpost = caseStoreRecordsWithMoreThanOneEntry
            .filter { entry -> entry.value.all { it.journalpostId.isNullOrEmpty() } }
            .also { log.info("${it.size} rina cases in case store with more than 1 record and no journalpost") }
        return caseStoreRecordsByRinasak.size
    }
}
