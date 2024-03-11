package eux.nav.rinasak.datasync.integration.dokarkiv.model

data class DokarkivJournalpostOppdatering(
    val tema: String,
    val bruker: DokarkivBruker,
    val sak: DokarkivSakOppdatering
)

data class DokarkivJournalpostSakOppdatering(
    val tema: String,
    val sak: DokarkivSakOppdatering
)
