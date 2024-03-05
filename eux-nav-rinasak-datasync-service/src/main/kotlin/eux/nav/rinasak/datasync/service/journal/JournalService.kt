package eux.nav.rinasak.datasync.service.journal

import eux.nav.rinasak.datasync.integration.eux.journal.JournalClient
import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxSedOversiktV3
import eux.nav.rinasak.datasync.integration.navrinasak.DokumentCreateType
import eux.nav.rinasak.datasync.integration.navrinasak.DokumentType
import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakClient
import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakDokumentClient
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.integration.saf.SafJournalpost
import eux.nav.rinasak.datasync.service.navrinasak.tilSedId
import eux.nav.rinasak.datasync.service.navrinasak.tilSedVersjon
import eux.nav.rinasak.datasync.service.navrinasak.uuid
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class JournalService(
    val safClient: SafClient,
    val euxRinaApiClient: EuxRinaApiClient,
    val navRinasakClient: NavRinasakClient,
    val navRinasakDokumentClient: NavRinasakDokumentClient,
    val journalClient: JournalClient
) {

    val log = KotlinLogging.logger {}

    fun journal(journalposter: List<String>) {
        log.info { "Journalfører ${journalposter.size} journalposter..." }
        journalposter
            .mapNotNull { safJournalpost(it) }
            .forEach { journal(it) }
        log.info { "Journalføring ferdig" }
    }

    fun journal(journalpost: SafJournalpost) {
        val rinasakId = journalpost.rinasakId()
        val navRinasak = navRinasakClient.finnNavRinasakOrNull(rinasakId)
        val eksisterendeNavRinasakDokument = navRinasak
            ?.dokumenter
            ?.firstOrNull()
        if (eksisterendeNavRinasakDokument == null) {
            log.info { "Ferdigstiller uten eksisterende dokument" }
        } else {
            euxRinaApiClient
                .euxRinaSakOversikt(rinasakId)
                .sedListe
                .forEach {
                    it.leggTilSedINavRinasak(
                        eksisterendeNavRinasakDokument = eksisterendeNavRinasakDokument,
                        eksisterendeDokumenter = navRinasak.dokumenter!!,
                        journalpost = journalpost,
                        rinasakId = rinasakId
                    )
                }
        }
        ferdigstill(journalpost.journalpostId)
        log.info { "Ferdigstilte journalpostId=${journalpost.journalpostId}" }
    }

    fun EuxSedOversiktV3.leggTilSedINavRinasak(
        eksisterendeNavRinasakDokument: DokumentType,
        eksisterendeDokumenter: List<DokumentType>,
        journalpost: SafJournalpost,
        rinasakId: Int
    ) {
        val eksternReferanseId = journalpost.eksternReferanseId
        val sedIdFraEksternReferanseId = tilSedId(eksternReferanseId)
        val sedIdFraEksternReferanseIdUuid = uuid(sedIdFraEksternReferanseId)
        val sedVersjonFraEksternReferanseId = tilSedVersjon(eksternReferanseId)
        val eksisterendeDokument = eksisterendeDokumenter
            .filter { UUID.fromString(it.sedId) == sedIdFraEksternReferanseIdUuid }
            .find { it.sedVersjon == sedVersjonFraEksternReferanseId }
        if (eksisterendeDokument != null) {
            log.info { "Dokument eksisterer allerede for journalpostId=${journalpost.journalpostId}" }
        } else {
            val dokument = DokumentCreateType(
                sedId = sedIdFraEksternReferanseIdUuid.toString(),
                sedVersjon = sedVersjonFraEksternReferanseId,
                sedType = sedType,
                dokumentInfoId = journalpost.dokumenter.first().dokumentInfoId
            )
            navRinasakDokumentClient.opprettNavRinasakDokument(rinasakId, dokument)
            log.info { "Opprettet dokument i rinasak $rinasakId for journalpostId ${journalpost.journalpostId}" }
        }
    }

    fun ferdigstill(journalpostId: String) {
        journalClient.ferdigstill(journalpostId)
    }

    fun safJournalpost(journalpostId: String): SafJournalpost? =
        try {
            safClient.safJournalpost(journalpostId)
        } catch (e: RuntimeException) {
            log.error(e) { "kunne ikke hente journalpost for journalpostId=$journalpostId" }
            null
        }

    fun SafJournalpost.rinasakId() =
        try {
            tilleggsopplysninger
                .first { it.nokkel == "rinaSakId" }
                .verdi
                .toInt()
        } catch (e: RuntimeException) {
            log.error(e) { "Kunne ikke hente rinaSakId for journalpostId=$journalpostId" }
            throw e
        }
}
