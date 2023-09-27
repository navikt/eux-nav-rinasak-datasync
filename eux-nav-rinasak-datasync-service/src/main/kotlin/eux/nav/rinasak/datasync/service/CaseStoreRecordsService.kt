package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreCase
import eux.nav.rinasak.datasync.integration.casestore.EuxCaseStoreClient
import eux.nav.rinasak.datasync.integration.saf.SafSakClient
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CaseStoreRecordsService(
    val euxCaseStoreClient: EuxCaseStoreClient,
    val repository: CaseStoreRecordRepository,
    val navRinasakService: NavRinasakService,
    val safSakClient: SafSakClient,
) {
    val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsService::class.java)

    fun cases(): List<CaseStoreRecord> = repository.findAll()

    fun EuxCaseStoreCase.toCaseStoreRecord() =
        CaseStoreRecord(
            caseStoreRecordUuid = UUID.randomUUID(),
            caseStoreId = id,
            rinasakId = rinaId?.toInt() ?: 0,
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
        .also { log.info("next size: ${it.size}") }
        .map { repository.save(it) }
        .size

    fun populateNavRinasakStaging(): Int {
        val caseStoreRecordsByRinasak = repository
            .findAll()
            .groupBy { it.rinasakId }
        val caseStoreRecordsWithOneEntry = caseStoreRecordsWithOneEntry(caseStoreRecordsByRinasak)
        val caseStoreRecordsWithMoreThanOneEntry = caseStoreRecordsWithMoreThanOneEntry(caseStoreRecordsByRinasak)
        stageCaseStoreRecordsWithOneEntryMissingJournalpostId(caseStoreRecordsWithOneEntry)
        stageCaseStoreRecordsWithOneEntryAndJournalpostId(caseStoreRecordsWithOneEntry)
        stageCaseStoreRecordsWithMoreThanOneEntryMissingJournalpost(caseStoreRecordsWithMoreThanOneEntry)
        stageCaseStoreRecordsWithMoreThanOneEntryWithJournalpost(caseStoreRecordsWithMoreThanOneEntry)
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
            .filter { !it.value.journalpostId.isNullOrEmpty() }
            .also { log.info("${it.size} rina cases in case store with 1 record and journalpostId") }

    fun stageCaseStoreRecordsWithMoreThanOneEntryWithJournalpost(
        caseStoreRecordsWithMoreThanOneEntry: Map<Int, List<CaseStoreRecord>>
    ) =
        caseStoreRecordsWithMoreThanOneEntry
            .filter { entry -> entry.value.any { !it.journalpostId.isNullOrEmpty() } }
            .also { log.info("${it.size} rina cases in case store with more than 1 record and journalpost") }

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
            stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId)
        else
            log.info("Rinasak $rinasakId har ikke fagsakId eller journalpostId og mer enn 1 case store record")
    }

    fun stageCaseStoreRecordWithOneEntryMissingJournalpostId(
        rinasakId: Int,
        record: CaseStoreRecord
    ) {
        val fagsakId = record.fagsakId
        if (fagsakId != null)
            stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId)
        else
            log.info("Rinasak $rinasakId har ikke fagsakId eller journalpostId og 1 case store record")
    }

    fun stageCaseStoreRecordWithMissingJournalpostId(rinasakId: Int, fagsakId: String) {
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        val safSak = safSakClient.safSak(fagsakId)
        val initiellFagsak = InitiellFagsak(
            id = fagsakId,
            tema = safSak.tema,
            system = safSak.fagsaksystem,
            nr = safSak.arkivsaksnummer,
            type = safSak.sakstype
        )
        navRinasakService.save(navRinasak)
        navRinasakService.save(initiellFagsak)
    }

}
