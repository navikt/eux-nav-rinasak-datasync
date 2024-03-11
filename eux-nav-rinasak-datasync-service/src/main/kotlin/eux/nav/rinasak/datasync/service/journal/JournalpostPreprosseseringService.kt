package eux.nav.rinasak.datasync.service.journal

import eux.nav.rinasak.datasync.integration.dokarkiv.client.DokarkivClient
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivBruker
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivBrukerType
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivJournalpostOppdatering
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivSakOppdatering
import eux.nav.rinasak.datasync.integration.saf.SafBruker
import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.service.mdc.mdc
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class JournalpostPreprosseseringService(
    val dokarkivClient: DokarkivClient,
    val safClient: SafClient
) {

    val log = logger {}

    fun settSak(journalposter: List<String>) {
        journalposter.forEach { oppdaterSak(it) }
    }

    fun oppdaterSak(journalpostId: String) {
        mdc(journalpostId = journalpostId)
        val bruker = safClient
            .safJournalpost(journalpostId)
            .bruker
            ?: throw RuntimeException("Fant ikke bruker")
        val oppdatering = oppdatering(journalpostId, bruker)
        if (oppdatering != null) {
            log.info { "Starter særskilt behandling av journalpost" }
            try {
                dokarkivClient.oppdater(journalpostId, oppdatering)
                log.info { "Særskilt behandling utført på journalpost" }
            } catch (e: RuntimeException) {
                log.error(e) { "Særskilt behandling feilet" }
                throw e
            }
        }
    }

    fun oppdatering(journalpostId: String, bruker: SafBruker) =
        when (journalpostId) {
            "476493871" -> dokarkivJournalpostOppdatering("FOR", bruker)
            else -> null
        }

    fun dokarkivJournalpostOppdatering(tema: String, bruker: SafBruker) =
        DokarkivJournalpostOppdatering(
            tema = tema,
            sak = DokarkivSakOppdatering(
                sakstype = "GENERELL_SAK",
                fagsaksystem = null,
                fagsakId = null
            ),
            bruker = DokarkivBruker(
                idType = DokarkivBrukerType.valueOf(bruker.type),
                id = bruker.id
            )
        )
}
