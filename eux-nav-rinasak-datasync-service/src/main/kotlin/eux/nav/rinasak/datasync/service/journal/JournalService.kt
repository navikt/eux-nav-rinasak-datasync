package eux.nav.rinasak.datasync.service.journal

import eux.nav.rinasak.datasync.integration.saf.SafClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class JournalService(
    val safClient: SafClient
) {

    val log = KotlinLogging.logger {}

    fun journal(journalposter: List<String>) {
        journalposter
            .forEach {
                val journalpost = safClient.safJournalpost(it)
                log.info { "journalpost: $journalpost" }
            }
    }
}