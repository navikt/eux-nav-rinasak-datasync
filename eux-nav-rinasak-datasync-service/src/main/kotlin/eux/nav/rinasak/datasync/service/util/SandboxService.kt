package eux.nav.rinasak.datasync.service.util

import eux.nav.rinasak.datasync.integration.eux.rinaapi.EuxRinaApiClient
import eux.nav.rinasak.datasync.integration.saf.SafClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SandboxService(
    val euxRinaApiClient: EuxRinaApiClient,
    val safClient: SafClient
) {
    val log: Logger = LoggerFactory.getLogger(SandboxService::class.java)

    fun sandbox() {
        log.info("Sandbox: Henter 454015590...")
        val journalpost = safClient.safJournalpost("454015590")
        log.info("Sandbox: Fant $journalpost...")
        val dokumentInfoId = journalpost.dokumenter.first().dokumentInfoId
        log.info("Sandbox: Henter $dokumentInfoId...")
        val nyesteJournalpost = safClient.tilknyttedeJournalposterRoot(dokumentInfoId)
        log.info("Sandbox: Fant $nyesteJournalpost")
        val firstTilknyttetJournalpostOrNull =  safClient.firstTilknyttetJournalpostOrNull(dokumentInfoId)
        log.info("Sandbox: Fant by first: $firstTilknyttetJournalpostOrNull")
        val fnr = euxRinaApiClient.fnrOrNull(1444381)
        log.info("fnr: $fnr")
    }
}