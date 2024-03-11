package eux.nav.rinasak.datasync.service.journal

import eux.nav.rinasak.datasync.integration.dokarkiv.client.DokarkivClient
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivJournalpostSakOppdatering
import eux.nav.rinasak.datasync.integration.dokarkiv.model.DokarkivSakOppdatering
import eux.nav.rinasak.datasync.service.mdc.mdc
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class JournalpostPreprosseseringService(
    val dokarkivClient: DokarkivClient
) {

    val log = logger {}

    fun settSak(journalposter: List<String>) {
        journalposter.forEach { oppdaterSak(it) }
    }

    fun oppdaterSak(journalpostId: String) {
        mdc(journalpostId = journalpostId)
        val oppdatering = oppdatering(journalpostId)
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

    fun oppdatering(journalpostId: String) =
        when (journalpostId) {
            "476493871" -> dokarkivJournalpostSakOppdatering("FOR")
            else -> null
        }

    fun dokarkivJournalpostSakOppdatering(tema: String) =
        DokarkivJournalpostSakOppdatering(
            tema = tema,
            sak = DokarkivSakOppdatering(
                sakstype = "GENERELL_SAK",
                fagsaksystem = null,
                fagsakId = null
            )
        )
}
