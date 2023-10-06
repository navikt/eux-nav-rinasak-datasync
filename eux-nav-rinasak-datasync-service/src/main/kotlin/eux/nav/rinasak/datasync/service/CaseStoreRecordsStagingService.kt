package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.integration.saf.SafSak
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.*
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CaseStoreRecordsStagingService(
    val navRinasakService: NavRinasakService,
    val safClient: SafClient,
    val dokumentService: DokumentService,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
    val euxRinaApiClient: EuxRinaApiClient,
) {

    @Transactional
    fun stageCaseStoreRecordWithMissingJournalpostId(rinasakId: Int, fagsakId: String, record: CaseStoreRecord) {
        stageCaseStoreRecordWithMissingJournalpostId(rinasakId, fagsakId, listOf(record))
    }

    @Transactional
    fun stageCaseStoreRecordWithMissingJournalpostId(rinasakId: Int, fagsakId: String, records: List<CaseStoreRecord>) {
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        val fnr = euxRinaApiClient.fnrOrNull(rinasakId)
        if (fnr != null)
            stageCaseStoreRecordWithMissingJournalpostId(
                safSak = safClient.safSakOrNull(fnr, fagsakId),
                navRinasak = navRinasak,
                fagsakId = fagsakId,
                records = records
            )
        else
            records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = RINASAK_NOT_FOUND)) }
    }

    private fun stageCaseStoreRecordWithMissingJournalpostId(
        safSak: SafSak?,
        navRinasak: NavRinasak,
        fagsakId: String,
        records: List<CaseStoreRecord>
    ) {
        if (safSak != null) {
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
        } else {
            records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = FAGSAK_NOT_FOUND)) }
        }
    }

    @Transactional
    fun stageCaseStoreRecordWithOneEntryAndJournalpostId(
        rinasakId: Int,
        record: CaseStoreRecord
    ) {
        val journalpostId = record.journalpostId
            ?: throw RuntimeException("kodefeil relatert til rinasakid: $rinasakId, mangler journalpostId")
        val navRinasak = NavRinasak(rinasakId = rinasakId)
        val dokument = dokumentService.dokument(
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
            .map { dokumentService.dokument(it, navRinasak.navRinasakUuid) }
            .forEach { navRinasakService.save(it) }
        records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = SYNCED)) }
    }
}
