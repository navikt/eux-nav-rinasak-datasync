package eux.nav.rinasak.datasync.service.journal

import eux.nav.rinasak.datasync.integration.dokarkiv.client.DokarkivClient
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivBruker
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivBrukerType
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivJournalpostOppdatering
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivSakOppdatering
import eux.nav.rinasak.datasync.integration.eux.journal.JournalClient
import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxSedOversiktV3
import eux.nav.rinasak.datasync.integration.navrinasak.*
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.integration.saf.SafJournalpost
import eux.nav.rinasak.datasync.integration.saf.SafSak
import eux.nav.rinasak.datasync.service.mdc.clearMdc
import eux.nav.rinasak.datasync.service.mdc.mdc
import eux.nav.rinasak.datasync.service.navrinasak.tilSedId
import eux.nav.rinasak.datasync.service.navrinasak.tilSedVersjon
import eux.nav.rinasak.datasync.service.navrinasak.uuid
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class JournalService(
    val safClient: SafClient,
    val euxRinaApiClient: EuxRinaApiClient,
    val navRinasakClient: NavRinasakClient,
    val navRinasakDokumentClient: NavRinasakDokumentClient,
    val journalClient: JournalClient,
    val dokarkivClient: DokarkivClient
) {

    val log = logger {}

    fun journal(journalposter: List<String>) {
        log.info { "Journalfører ${journalposter.size} journalposter..." }
        journalposter
            .mapNotNull { safJournalpost(it) }
            .forEach { it.tryJournal() }
        log.info { "Journalføring ferdig" }
    }

    fun SafJournalpost.tryJournal() =
        try {
            mdc(journalpostId = journalpostId)
            journal(this)
        } catch (e: RuntimeException) {
            log.error(e) { "Kunne ikke journalføre $journalpostId" }
        } finally {
            clearMdc()
        }

    fun journal(journalpost: SafJournalpost) {
        val rinasakId = journalpost.rinasakId
        mdc(rinasakId = rinasakId)
        val navRinasak = navRinasakClient.finnNavRinasakOrNull(rinasakId)
        if (navRinasak == null)
            journalpost.feilregistrer()
        else
            journalpost.journal(navRinasak)
    }

    fun SafJournalpost.feilregistrer() {
        journalClient.settStatusAvbryt(listOf(journalpostId))
        log.info { "Feilregistrerte $journalpostId" }
    }

    fun SafJournalpost.journal(rinasak: NavRinasakType) {
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
                        eksisterendeDokumenter = navRinasak.dokumenter!!,
                        journalpost = this,
                        rinasakId = rinasakId
                    )
                }
        }
        val eksisterendeAnnetDokumentMedSak = navRinasak!!
            .dokumenter!!
            .mapNotNull { safClient.firstTilknyttetJournalpostOrNull(it.dokumentInfoId!!) }
            .first { it.sak != null }
        oppdater(eksisterendeAnnetDokumentMedSak)
        ferdigstill(journalpostId)
        log.info { "Ferdigstilte journalpostId=$journalpostId" }
    }

    fun EuxSedOversiktV3.leggTilSedINavRinasak(
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
            .firstOrNull { it.sedVersjon == sedVersjonFraEksternReferanseId }
        if (eksisterendeDokument != null) {
            log.info { "Dokument eksisterer allerede for journalpostId=${journalpost.journalpostId}" }
        } else {
            val dokument = DokumentCreateType(
                sedId = sedIdFraEksternReferanseIdUuid.toString(),
                sedVersjon = sedVersjonFraEksternReferanseId,
                sedType = sedType,
                dokumentInfoId = journalpost.dokumenter.first().dokumentInfoId
            )
            tryOpprettNavRinasakDokument(rinasakId, dokument)
            log.info { "Opprettet dokument i rinasak $rinasakId for journalpostId ${journalpost.journalpostId}" }
        }
    }

    fun tryOpprettNavRinasakDokument(rinasakId: Int, dokumentCreateType: DokumentCreateType) =
        try {
            navRinasakDokumentClient.opprettNavRinasakDokument(rinasakId, dokumentCreateType)
        } catch (e: RuntimeException) {
            log.warn { "Dokumentet finnes alt i NAV Rinasak: ${dokumentCreateType.dokumentInfoId}" }
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

    val SafJournalpost.rinasakId
        get() =
            try {
                tilleggsopplysninger
                    .first { it.nokkel == "rinaSakId" }
                    .verdi
                    .toInt()
            } catch (e: RuntimeException) {
                log.error(e) { "Kunne ikke hente rinaSakId for journalpostId=$journalpostId" }
                throw e
            }

    fun SafJournalpost.oppdater(eksisterendeJournalpost: SafJournalpost) {
        log.info { "Oppdaterer $journalpostId med utgangspunkt i ${eksisterendeJournalpost.journalpostId}" }
        val eksisterendeSak = eksisterendeJournalpost.sak!!
        val eksisterendeBruker = eksisterendeJournalpost.bruker!!
        val dokarkivBruker = DokarkivBruker(
            id = eksisterendeBruker.id,
            idType = DokarkivBrukerType.valueOf(eksisterendeBruker.type)
        )
        val dokarkivSak =
            when {
                eksisterendeSak.sakstype == "GENERELL_SAK" -> dokarkivSakOppdateringGenerell()
                else -> eksisterendeSak.dokarkivSakOppdateringFagsak()
            }
        val dokarkivOppdatering = DokarkivJournalpostOppdatering(
            sak = dokarkivSak,
            bruker = dokarkivBruker,
            tema = eksisterendeSak.tema!!
        )
        dokarkivClient.oppdater(journalpostId, dokarkivOppdatering)
        log.info { "Journalpost oppdatert" }
    }

    fun dokarkivSakOppdateringGenerell() =
        DokarkivSakOppdatering(
            sakstype = "GENERELL_SAK",
            fagsaksystem = null,
            fagsakId = null
        )

    fun SafSak.dokarkivSakOppdateringFagsak() =
        DokarkivSakOppdatering(
            sakstype = sakstype!!,
            fagsaksystem = fagsaksystem,
            fagsakId = fagsakId
        )
}
