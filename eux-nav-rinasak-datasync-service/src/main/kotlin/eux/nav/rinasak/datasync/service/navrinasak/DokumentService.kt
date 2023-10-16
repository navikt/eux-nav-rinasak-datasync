package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.integration.saf.SafClient
import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentService(
    val safClient: SafClient,
    val dokumentRepository: DokumentRepository
) {
    val log: Logger = LoggerFactory.getLogger(DokumentService::class.java)

    fun dokumenter(): List<Dokument> = dokumentRepository.findAll()

    fun dokumenterPending(): List<Dokument> = dokumentRepository.findAllBySyncStatus(PENDING)

    fun dokumentOrNull(
        journalpostId: String,
        navRinasakUuid: UUID
    ): Dokument? {
        try {
            val journalpost = safClient.safJournalpost(journalpostId)
            log.info("Fant journalpost: $journalpost")
            val sedId = tilSedId(journalpost.eksternReferanseId)
            val sedVersjon = tilSedVersjon(journalpost.eksternReferanseId)
            val safDokument = journalpost
                .dokumenter
                .firstOrNull()
                ?: throw RuntimeException("Ingen dokumenter knyttet til journalpost: $journalpostId")
            return Dokument(
                navRinasakUuid = navRinasakUuid,
                dokumentInfoId = safDokument.dokumentInfoId,
                sedId = sedId,
                sedVersjon = sedVersjon,
                sedType = safDokument.brevkode ?: "brevkode mangler",
            )
        } catch (e: RuntimeException) {
            log.error("Kunne ikke hente dokument for journalpostId: $journalpostId ($navRinasakUuid)", e)
            return null
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

    private fun tilSedVersjon(eksternReferanseId: String): Int {
        return try {
            eksternReferanseId
                .split("_".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[2]
                .toInt()
        } catch (e: RuntimeException) {
            log.info("Ugyldig format på ekstern referanse id for sed versjon {}", eksternReferanseId, e)
            -1
        }
    }
}