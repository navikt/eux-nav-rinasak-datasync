package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.SYNCED
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CaseStoreRecordsStagingService(
    val navRinasakService: NavRinasakService,
    val safClient: SafClient,
    val dokumentInfoIdService: DokumentInfoIdService,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
) {

    @Transactional
    fun stageCaseStoreRecordWithMissingJournalpostId(rinasakId: Int, fagsakId: String, record: CaseStoreRecord) {
        stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId, listOf(record))
    }

    @Transactional
    fun stageCaseStoreRecordWithMissingJournalpostId(rinasakId: Int, fagsakId: String, records: List<CaseStoreRecord>) {
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        val safSak = safClient.safSak(fagsakId)
        val initiellFagsak = InitiellFagsak(
            navRinasakUuid = navRinasak.navRinasakUuid,
            id = fagsakId,
            tema = safSak.tema,
            system = safSak.fagsaksystem,
            nr = safSak.arkivsaksnummer,
            type = safSak.sakstype
        )
        navRinasakService.save(navRinasak)
        navRinasakService.save(initiellFagsak)
        records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = SYNCED)) }
    }

    @Transactional
    fun stageCaseStoreRecordWithOneEntryAndJournalpostId(
        rinasakId: Int,
        record: CaseStoreRecord
    ) {
        val journalpostId = record.journalpostId
            ?: throw RuntimeException("kodefeil relatert til rinasakid: $rinasakId, mangler journalpostId")
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        val dokument = dokumentInfoIdService.dokument(
            journalpostId = journalpostId,
            navRinasakUuid = navRinasak.navRinasakUuid
        )
        navRinasakService.save(navRinasak)
        navRinasakService.save(dokument)
        caseStoreRecordRepository.save(record.copy(syncStatus = SYNCED))
    }

    @Transactional
    fun stageCaseStoreRecordsWithMoreThanOneEntryWithJournalpost(
        rinasakId: Int,
        records: List<CaseStoreRecord>
    ) {
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        navRinasakService.save(navRinasak)
        records
            .mapNotNull { it.journalpostId }
            .map { dokumentInfoIdService.dokument(it, navRinasak.navRinasakUuid) }
            .forEach { navRinasakService.save(it) }
        records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = SYNCED)) }
    }
}