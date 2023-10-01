package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.saf.SafSakClient
import eux.nav.rinasak.datasync.model.Dokument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentInfoIdService(
    val safSakClient: SafSakClient
) {
    val log: Logger = LoggerFactory.getLogger(DokumentInfoIdService::class.java)

    fun dokument(
        journalpostId: String,
        navRinasakUuid: UUID
    ): Dokument {
        try {
            val journalpost = safSakClient.safJournalpost(journalpostId)
            val sedId = tilSedId(journalpost.eksternReferanseId)
            val safDokument = journalpost.dokumenter.firstOrNull()
            return Dokument(
                navRinasakUuid = navRinasakUuid,
                dokumentInfoId = safDokument?.dokumentInfoId ?: "mangler",
                sedId = sedId,
                sedType = "tbd",
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