package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.model.Dokument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentService(
    val safClient: SafClient
) {
    val log: Logger = LoggerFactory.getLogger(DokumentService::class.java)

    fun dokument(
        journalpostId: String,
        navRinasakUuid: UUID
    ): Dokument {
        try {
            val journalpost = safClient.safJournalpost(journalpostId)
            log.info("Fant journalpost: $journalpost")
            val sedId = tilSedId(journalpost.eksternReferanseId)
            val safDokument = journalpost.dokumenter.firstOrNull()
            //TODO via via via
            return Dokument(
                navRinasakUuid = navRinasakUuid,
                dokumentInfoId = safDokument?.dokumentInfoId ?: "mangler",
                sedId = sedId,
                sedType = safDokument?.brevkode ?: "mangler",
            )
        } catch (e: RuntimeException) {
            log.error("Feilet i uthenting av navRinasakUuid $navRinasakUuid, journalpostId: $journalpostId", e)
            return Dokument(
                navRinasakUuid = navRinasakUuid,
                dokumentInfoId = "mangler",
                sedId = "mangler",
                sedType = "tbd",
            )
        }
    }

    private fun tilSedId(eksternReferanseId: String): String {
        return try {
            eksternReferanseId
                .split("_".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        } catch (e: RuntimeException) {
            log.info("Ugyldig format på ekstern referanse id {}", eksternReferanseId, e)
            eksternReferanseId
        }
    }
}