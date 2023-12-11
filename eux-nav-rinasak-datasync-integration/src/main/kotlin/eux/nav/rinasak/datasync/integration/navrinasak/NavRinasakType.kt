package eux.nav.rinasak.datasync.integration.navrinasak

data class NavRinasakType(
    val rinasakId: Int,
    val opprettetBruker: String,
    val opprettetTidspunkt: java.time.OffsetDateTime,
    val overstyrtEnhetsnummer: String? = null,
    val initiellFagsak: FagsakType? = null,
    val dokumenter: List<DokumentType>? = null
)

