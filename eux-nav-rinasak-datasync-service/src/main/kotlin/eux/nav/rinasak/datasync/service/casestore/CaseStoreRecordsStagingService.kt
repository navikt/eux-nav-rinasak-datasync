package eux.nav.rinasak.datasync.service.casestore

import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.integration.saf.SafSak
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.*
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.service.navrinasak.DokumentService
import eux.nav.rinasak.datasync.service.navrinasak.NavRinasakService
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
        val navRinasak = NavRinasak(
            rinasakId = rinasakId,
            overstyrtEnhetsnummer = records.overstyrtEnhetsnummer()
        )
        val fnr = euxRinaApiClient.fnrOrNull(rinasakId)
        if (fnr != null)
            stageCaseStoreRecordWithMissingJournalpostId(
                safSak = safClient.safSakOrNull(fnr, fagsakId),
                navRinasak = navRinasak,
                fnr = fnr,
                records = records
            )
        else
            records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = RINASAK_NOT_FOUND)) }
    }

    private fun stageCaseStoreRecordWithMissingJournalpostId(
        safSak: SafSak?,
        navRinasak: NavRinasak,
        fnr: String,
        records: List<CaseStoreRecord>
    ) {
        if (safSak != null) {
            val system = if (safSak.sakstype == "GENERELL_SAK")
                null
            else
                safSak.fagsaksystem
            val initiellFagsak = InitiellFagsak(
                navRinasakUuid = navRinasak.navRinasakUuid,
                id = safSak.arkivsaksnummer,
                tema = safSak.tema,
                system = system,
                nr = safSak.fagsakId,
                type = safSak.sakstype,
                fnr = fnr,
                arkiv = safSak.arkivsaksystem
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
        val navRinasak = NavRinasak(
            rinasakId = rinasakId,
            overstyrtEnhetsnummer = record.overstyrtEnhetsnummer
        )
        val dokument = dokumentService.dokumentOrNull(
            journalpostId = journalpostId,
            navRinasakUuid = navRinasak.navRinasakUuid
        )
        navRinasakService.save(navRinasak)
        dokument?.let { navRinasakService.save(it) }
        caseStoreRecordRepository.save(record.copy(syncStatus = SYNCED))
    }

    @Transactional
    fun stageCaseStoreRecordsWithMoreThanOneEntryWithJournalposts(
        rinasakId: Int,
        records: List<CaseStoreRecord>
    ) {
        val navRinasak = NavRinasak(
            rinasakId = rinasakId,
            overstyrtEnhetsnummer = records.overstyrtEnhetsnummer()
        )
        navRinasakService.save(navRinasak)
        records
            .mapNotNull { it.journalpostId }
            .mapNotNull { dokumentService.dokumentOrNull(it, navRinasak.navRinasakUuid) }
            .forEach { navRinasakService.save(it) }
        records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = SYNCED)) }
    }

    fun List<CaseStoreRecord>.overstyrtEnhetsnummer() = firstNotNullOfOrNull { it.overstyrtEnhetsnummer }
}
