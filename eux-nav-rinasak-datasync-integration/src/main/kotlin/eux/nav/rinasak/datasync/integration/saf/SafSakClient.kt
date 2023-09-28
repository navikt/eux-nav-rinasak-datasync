package eux.nav.rinasak.datasync.integration.saf

import org.springframework.stereotype.Component

@Component
class SafSakClient {

    fun safSak(fagsakId: String) =
        SafSak(
            fagsakId = fagsakId,
        )

    fun safJournalpost(journalpostId: String) =
        SafJournalpost(
            journalpostId = journalpostId
        )
}