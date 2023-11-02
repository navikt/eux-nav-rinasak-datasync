package eux.nav.rinasak.datasync.service.casestore

import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.integration.saf.SafSak
import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.*
import eux.nav.rinasak.datasync.model.exception.InvalidEksternReferanseIdException
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.service.navrinasak.DokumentService
import eux.nav.rinasak.datasync.service.navrinasak.NavRinasakService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CaseStoreRecordsStagingService(
    val navRinasakService: NavRinasakService,
    val safClient: SafClient,
    val dokumentService: DokumentService,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
    val euxRinaApiClient: EuxRinaApiClient,
) {
    val log: Logger = LoggerFactory.getLogger(DokumentService::class.java)

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
        val fnr = euxRinaApiClient.fnrOrNull(rinasakId)?.trim()
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
                system = system,
                tema = safSak.tema,
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
        val dokument = dokument(journalpostId, rinasakId, navRinasak.navRinasakUuid)
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
            .mapNotNull { dokument(it, rinasakId, navRinasak.navRinasakUuid) }
            .forEach { navRinasakService.save(it) }
        records.forEach { caseStoreRecordRepository.save(it.copy(syncStatus = SYNCED)) }
    }

    fun dokument(journalpostId: String, rinasakId: Int, navRinasakUuid: UUID): Dokument? {
        val errorKeys = "rinasakid=$rinasakId, journalpostId=$journalpostId, navRinasakUuid=$navRinasakUuid"
        return try {
            dokumentService.dokument(journalpostId, navRinasakUuid)
        } catch (e: InvalidEksternReferanseIdException) {
            log.error("Feil format på ekstern referanse id. $errorKeys", e)
            null
        } catch (e: RuntimeException) {
            log.error("Uventet feil i mapping av dokument. $errorKeys", e)
            null
        }
    }

    fun List<CaseStoreRecord>.overstyrtEnhetsnummer() = firstNotNullOfOrNull { it.overstyrtEnhetsnummer }
}
