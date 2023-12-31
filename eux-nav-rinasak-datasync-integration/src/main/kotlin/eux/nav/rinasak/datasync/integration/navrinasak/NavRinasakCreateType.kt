package eux.nav.rinasak.datasync.integration.navrinasak

data class NavRinasakCreateType(
    val rinasakId: Int,
    val opprettetBruker: String = "migrering",
    val overstyrtEnhetsnummer: String? = null,
    val initiellFagsak: NavRinasakInitiellFagsakCreateType? = null,
    val dokumenter: List<NavRinasakDokumentCreateType>? = null
)
